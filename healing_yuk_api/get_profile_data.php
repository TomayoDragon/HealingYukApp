<?php
require_once 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = $_POST['user_id'];

    if (empty($user_id)) {
        echo json_encode(['status' => 'error', 'message' => 'User ID tidak valid.']);
        die();
    }

    // Query pertama: mengambil data user
    $stmt_user = $conn->prepare("SELECT name, email, join_date FROM users WHERE id = ?");
    $stmt_user->bind_param("i", $user_id);
    $stmt_user->execute();
    $result_user = $stmt_user->get_result();
    $user_data = $result_user->fetch_assoc();
    $stmt_user->close();

    // Query kedua: menghitung jumlah favorit
    $stmt_fav = $conn->prepare("SELECT COUNT(*) as total_favourites FROM favourites WHERE user_id = ?");
    $stmt_fav->bind_param("i", $user_id);
    $stmt_fav->execute();
    $result_fav = $stmt_fav->get_result();
    $fav_data = $result_fav->fetch_assoc();
    $stmt_fav->close();

    // Gabungkan hasil dari kedua query
    $response_data = [
        'name' => $user_data['name'],
        'email' => $user_data['email'],
        // Format tanggal agar lebih mudah dibaca
        'join_date' => date_format(date_create($user_data['join_date']), "d/m/Y"),
        'total_favourites' => $fav_data['total_favourites']
    ];

    echo json_encode([
        'status' => 'success',
        'data' => $response_data
    ]);

} else {
    echo json_encode(['status' => 'error', 'message' => 'Metode request tidak valid.']);
}

$conn->close();
?>