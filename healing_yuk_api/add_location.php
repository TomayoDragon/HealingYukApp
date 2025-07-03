<?php
require_once 'connection.php';

// Pastikan request yang masuk adalah metode POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    // Ambil semua data yang dikirim dari aplikasi Android
    $name = $_POST['name'];
    $image_url = $_POST['image_url'];
    $short_description = $_POST['short_description'];
    $category = $_POST['category'];
    $full_description = $_POST['full_description'];

    // Validasi sederhana di sisi server
    if (empty($name) || empty($image_url) || empty($short_description) || empty($category) || empty($full_description)) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Semua kolom harus diisi.'
        ]);
        die();
    }

    // Siapkan query INSERT menggunakan prepared statement untuk keamanan
    $stmt = $conn->prepare("INSERT INTO locations (name, image_url, short_description, category, full_description) VALUES (?, ?, ?, ?, ?)");
    
    // Bind parameter ke query
    // "sssss" berarti kelima parameter adalah tipe data String
    $stmt->bind_param("sssss", $name, $image_url, $short_description, $category, $full_description);

    // Eksekusi query
    if ($stmt->execute()) {
        // Jika berhasil, kirim respon sukses
        echo json_encode([
            'status' => 'success',
            'message' => 'Lokasi baru berhasil ditambahkan.'
        ]);
    } else {
        // Jika gagal, kirim respon error
        echo json_encode([
            'status' => 'error',
            'message' => 'Gagal menambahkan lokasi: ' . $stmt->error
        ]);
    }

    $stmt->close();
} else {
    // Jika metode request bukan POST
    echo json_encode([
        'status' => 'error',
        'message' => 'Metode request tidak valid.'
    ]);
}

$conn->close();
?>