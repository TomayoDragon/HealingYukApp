<?php
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];
    $location_id = $_POST['location_id'];

    if (empty($user_id) || empty($location_id)) {
        echo json_encode(['status' => 'error', 'message' => 'User ID dan Location ID tidak boleh kosong.']);
        die();
    }

    // Siapkan query DELETE menggunakan prepared statement
    $stmt = $conn->prepare("DELETE FROM favourites WHERE user_id = ? AND location_id = ?");
    $stmt->bind_param("ii", $user_id, $location_id);

    if ($stmt->execute()) {
        // Cek apakah ada baris yang terhapus
        if ($stmt->affected_rows > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Berhasil dihapus dari favorit.']);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Lokasi tidak ditemukan di favorit Anda.']);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Gagal menghapus dari favorit: ' . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>