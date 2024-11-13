let selectedSeatId = null;

function selectSeat(seatElement) {
    const seatID = seatElement.getAttribute('data-seat-id');
    const seatIDInput = seatElement.querySelector('.seat-id-input');
 	const isBooked = seatElement.getAttribute('data-is-booked') === 'true';
 	if (isBooked) {
            alert("Ghế này đã được đặt!");
            return;
        }
    // Nếu ghế đã được chọn
    if (seatElement.classList.contains('seat-selected')) {
        // Bỏ chọn ghế
        seatElement.classList.remove('seat-selected');
        selectedSeatId = null; // Reset selectedSeatId
    } else {
        // Chọn ghế
        seatElement.classList.add('seat-selected');
        selectedSeatId = seatID; // Cập nhật selectedSeatId
    }

    // Điền giá trị seatID vào thẻ input ẩn
    seatIDInput.value = selectedSeatId;
}

function addSeat(seat) {
    const isBooked = seat.getAttribute('data-is-booked') === 'true';
    if (!isBooked) {
        seat.classList.add('seat-selected');
        selectedSeatId = seat.getAttribute('data-seat-id');
    }    
}

function removeSeat(seat) {
    const isBooked = seat.getAttribute('data-is-booked') === 'false';
    if (!isBooked) {
        seat.classList.remove('seat-selected');
        selectedSeatId = null; // Reset selectedSeatId khi bỏ chọn
    }    
}

$(document).ready(function() {
    $(".route-link").click(function(event) {
        event.preventDefault();
        var routeId = $(this).data("route-id");
        $.ajax({
            url: "/routes/" + routeId + "/seats",
            success: function(data) {
                $("#seats").html(data);
            },
            error: function(xhr, status, error) {
                console.error("Error: " + error);
                alert("Failed to load seats.");
            }
        });
    });
});
    $(document).ready(function() {
            $("#submitBtn").click(function(event) {
                console.log("Selected Seat ID:", selectedSeatId); // Kiểm tra giá trị
                if (selectedSeatId) {
                    $("input[name='seatID']").val(selectedSeatId);
                } else {
                    alert("Please select a seat before submitting.");
                    event.preventDefault(); // Ngăn chặn gửi form
                }
            });
        });

