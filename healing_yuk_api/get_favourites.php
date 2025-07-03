<?php
require_once 'connection.php';

// API ini akan menerima user_id melalui metode POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];

    if (empty($user_id)) {
        echo json_encode(['status' => 'error', 'message' => 'User ID tidak boleh kosong.']);
        die();
    }

    // Query SQL dengan INNER JOIN untuk mengambil data lokasi berdasarkan user_id di tabel favourites
    $stmt = $conn->prepare(
        "SELECT l.id, l.name, l.image_url, l.short_description, l.category, l.full_description 
         FROM locations l 
         INNER JOIN favourites f ON l.id = f.location_id 
         WHERE f.user_id = ?"
    );
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $response = [];
    if ($result->num_rows > 0) {
        $favourites = [];
        while($row = $result->fetch_assoc()) {
            $favourites[] = $row;
        }
        $response['status'] = 'success';
        $response['message'] = 'Data favorit berhasil diambil.';
        $response['data'] = $favourites;
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Anda belum memiliki lokasi favorit.';
    }

    echo json_encode($response);
    $stmt->close();

} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>