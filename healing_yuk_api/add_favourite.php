<?php
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ambil user_id dan location_id dari request
    $user_id = $_POST['user_id'];
    $location_id = $_POST['location_id'];

    // Validasi dasar
    if (empty($user_id) || empty($location_id)) {
        echo json_encode(['status' => 'error', 'message' => 'User ID dan Location ID tidak boleh kosong.']);
        die();
    }

    // Siapkan query untuk memasukkan data ke tabel favourites
    $stmt = $conn->prepare("INSERT INTO favourites (user_id, location_id) VALUES (?, ?)");
    // "ii" berarti kedua parameter adalah tipe data Integer
    $stmt->bind_param("ii", $user_id, $location_id);

    if ($stmt->execute()) {
        // Jika berhasil
        echo json_encode(['status' => 'success', 'message' => 'Berhasil ditambahkan ke favorit.']);
    } else {
        // Jika gagal, cek apakah karena data sudah ada (duplicate entry)
        if ($conn->errno == 1062) { // 1062 adalah kode error untuk duplicate entry
            echo json_encode(['status' => 'error', 'message' => 'Lokasi ini sudah ada di favorit Anda.']);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Gagal menambahkan ke favorit: ' . $stmt->error]);
        }
    }

    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>