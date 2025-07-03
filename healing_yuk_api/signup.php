<?php
// Memanggil file koneksi database
require_once 'connection.php';

// Hanya proses jika request method adalah POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ambil data dari POST request
    $email = $_POST['email'];
    $name = $_POST['name'];
    $pass = $_POST['password'];
    $repeat_password = $_POST['repeat_password'];

    // Validasi: Pastikan semua field terisi
    if (empty($email) || empty($name) || empty($pass) || empty($repeat_password)) {
        echo json_encode(['status' => 'error', 'message' => 'Semua field harus diisi.']);
        die();
    }

    // Validasi: Password dan Ulangi Password harus sama
    if ($pass !== $repeat_password) {
        echo json_encode(['status' => 'error', 'message' => 'Password dan Ulangi Password tidak sama.']);
        die();
    }

    // Validasi: Email tidak boleh kembar
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo json_encode(['status' => 'error', 'message' => 'Email sudah terdaftar.']);
        $stmt->close();
        $conn->close();
        die();
    }
    $stmt->close();

    // Hash password dan siapkan tanggal join
    $hashed_password = password_hash($pass, PASSWORD_DEFAULT);
    $join_date = date('Y-m-d H:i:s');

    // Masukkan user baru ke database
    $stmt = $conn->prepare("INSERT INTO users (email, name, password, join_date) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $email, $name, $hashed_password, $join_date);

    if ($stmt->execute()) {
        echo json_encode(['status' => 'success', 'message' => 'Pendaftaran berhasil.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Pendaftaran gagal: ' . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>