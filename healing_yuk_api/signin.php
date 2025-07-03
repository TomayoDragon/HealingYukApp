<?php
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['email'];
    $pass = $_POST['password'];

    if (empty($email) || empty($pass)) {
        echo json_encode(['status' => 'error', 'message' => 'Email dan password harus diisi.']);
        die();
    }

    $stmt = $conn->prepare("SELECT id, name, password, join_date FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($id, $name, $hashed_password, $join_date);
        $stmt->fetch();

        if (password_verify($pass, $hashed_password)) {
            $user_data = [
                'id' => $id,
                'name' => $name,
                'email' => $email,
                'join_date' => $join_date
            ];
            echo json_encode(['status' => 'success', 'message' => 'Login berhasil.', 'data' => $user_data]);
        } else {
            // PESAN ERROR SUDAH DISAMAKAN
            echo json_encode(['status' => 'error', 'message' => 'Email atau Password salah.']);
        }
    } else {
        // PESAN ERROR SUDAH DISAMAKAN
        echo json_encode(['status' => 'error', 'message' => 'Email atau Password salah.']);
    }

    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>