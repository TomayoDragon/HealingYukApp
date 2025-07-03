<?php
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];
    $old_password = $_POST['old_password'];
    $new_password = $_POST['new_password'];

    // Validasi dasar
    if (empty($user_id) || empty($old_password) || empty($new_password)) {
        echo json_encode(['status' => 'error', 'message' => 'Semua kolom harus diisi.']);
        die();
    }

    // 1. Ambil hash password saat ini dari database
    $stmt = $conn->prepare("SELECT password FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $current_hashed_password = $row['password'];

        // 2. Verifikasi password lama
        if (password_verify($old_password, $current_hashed_password)) {
            // Jika password lama benar, hash password baru
            $new_hashed_password = password_hash($new_password, PASSWORD_DEFAULT);

            // 3. Update password di database
            $update_stmt = $conn->prepare("UPDATE users SET password = ? WHERE id = ?");
            $update_stmt->bind_param("si", $new_hashed_password, $user_id);

            if ($update_stmt->execute()) {
                echo json_encode(['status' => 'success', 'message' => 'Password berhasil diubah.']);
            } else {
                echo json_encode(['status' => 'error', 'message' => 'Gagal mengubah password.']);
            }
            $update_stmt->close();
        } else {
            // Jika password lama salah
            echo json_encode(['status' => 'error', 'message' => 'Password lama Anda salah.']);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'User tidak ditemukan.']);
    }
    $stmt->close();

} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?> 