<?php
// Header untuk mengizinkan akses dari mana saja (Cross-Origin Resource Sharing)
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Detail koneksi database
$servername = "localhost";
$username = "native_160422100";
$password = "ubaya";
$dbname = "native_160422100";

// Membuat koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    // Jika koneksi gagal, hentikan script dan kirim pesan error
    echo json_encode(['status' => 'error', 'message' => 'Koneksi database gagal: ' . $conn->connect_error]);
    die();
}
?>