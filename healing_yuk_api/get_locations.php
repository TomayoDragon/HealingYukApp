<?php
require_once 'connection.php';

// Query untuk mengambil semua data dari tabel locations
$sql = "SELECT id, name, image_url, short_description, category, full_description FROM locations ORDER BY id DESC";
$result = $conn->query($sql);

$response = [];
if ($result->num_rows > 0) {
    $locations = [];
    // Looping untuk mengambil setiap baris data
    while($row = $result->fetch_assoc()) {
        $locations[] = $row;
    }
    // Jika berhasil, kirim status sukses dan data locations
    $response['status'] = 'success';
    $response['message'] = 'Data lokasi berhasil diambil.';
    $response['data'] = $locations;
} else {
    // Jika tidak ada data, kirim status error
    $response['status'] = 'error';
    $response['message'] = 'Tidak ada data lokasi.';
}

echo json_encode($response);
$conn->close();
?>