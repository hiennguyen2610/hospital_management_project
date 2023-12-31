async function loadAllSpeciality() {
    // Gán api vào biến
    var url = 'http://localhost:8080/api/v1/speciality/admin/find-all';

    // Sử dụng hàm fetch gửi yêu cầu GET đến API trên
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
        })
    });

    // Chuyển dữ liệu phản hồi về dạng Json và lưu vào biến mới
    var list = await response.json();
    var main = '<option value="">Tất cả chuyên khoa</option>';

    // Tạo vòng lặp để lấy danh sách chuyên khoa
    for (i = 0; i < list.length; i++) {
        // Lấy ra tên chuyên khoa tương ứng với id
        main += ` <option value="${list[i].id}">${list[i].name}</option>`
    }

    // Thay thế ô select bằng nội dung được chọn
    document.getElementById("listSpecialy").innerHTML = main
}

// Tìm kiếm bác sĩ
async function searchDoctors(page) {
    // Lấy giá trị trong ô search
    var param = document.getElementById("param").value
    // Lấy giá trị trong ô chọn chuyên khoa
    var special = document.getElementById("listSpecialy").value
    // Gán API vào biến (nếu ko chọn chuyên khoa)
    var url = 'http://localhost:8080/api/v1/doctor/admin/all-page?param='+param+"&size=3&page="+page;
    if(special != "" && special != null){
        // nếu chọn chuyên khoa
        url = 'http://localhost:8080/api/v1/doctor/admin/all-page?param='+param+'&idspecialy='+special+"&size=3&page="+page;
    }

    // Sử dụng hàm fetch gửi yêu cầu GET đến API trên
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
        })
    });

    // Lưu kq và chuyển thành json
    var result = await response.json();
    var list = result.content;
    var totalPage = result.totalPages
    var main = '';

    for (i = 0; i < list.length; i++) {
        var ck = '';
        for(j=0; j<list[i].specialities.length; j++){
            ck += list[i].specialities[j].name +"<br>"
        }
        main += `<tr>
                    <td>${list[i].user.name}</td>
                    <td>${ck}</td>
<!--                    TODO : chưa hiển thị được email và String name của doctorLevel, gender-->
<!--                    <td>${list[i].email}</td>-->
                    <td>${list[i].doctorLevel}</td>
                    <td>${list[i].user.gender}</td>
                    <td>${list[i].address}</td>
                    <td class="text-right">
                        <div class="d-flex justify-content-between">
                            <a class="btn btn-link p-0 submit-update-btn" onclick="loadDetail(${list[i].id})">
                                <i class="fa-solid fa-pencil fa-lg text-primary"></i>
                            </a>
                            <button onclick="deleteDoctor(${list[i].id})" class="btn btn-link p-0 mr-2 submit-delete-btn">
                                <span><i class="fa-solid fa-trash-can fa-lg text-danger"></i></span>
                            </button>
                        </div>
                    </td>
                </tr>`
    }
    document.getElementById("listDoctor").innerHTML = main
    var mainpage = ''
    for(i=1; i<= totalPage; i++){
        mainpage += '<li onclick="searchDoctors('+(Number(i)-1)+')" class="page-item"><a class="page-link">'+i+'</a></li>'
    }
    document.getElementById("listpage").innerHTML = mainpage
}

// Load thông tin doctor hiển thị lên form
async function loadDetail(doctorId){
    $.ajax({
        url: "/api/v1/admin/doctor/" + doctorId,
        type: "GET",
        success: function (response) {
            // $("#update-doctor #name").val(response.name)
            $("#update-doctor #phone").val(response.phone)
            $("#update-doctor #doctorLevel").val(response.doctorLevel)
            $("#update-doctor #address").val(response.address)
            $("#update-doctor #introduce").val(response.introduce)
            $("#update-doctor #iddoctorupdate").val(doctorId)
            $("#update-doctor #id").val(doctorId)
            let specialities = [];
            response.specialities.forEach(speciality => {
                specialities.push(speciality.id);
            });
            $("#update-doctor #speciality").val(specialities).trigger('change')
            $("#update-doctor-modal").modal("show");
        },
        error: function (response) {
            console.log(response)
        }
    })
}

// Xóa doctor
async function deleteDoctor(doctorId){
    Swal.fire({
        title: 'Bạn có chắc muốn xóa bác sĩ này không?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/api/v1/admin/doctor/' + doctorId,
                type: 'DELETE',
                success: function (response) {
                    Swal.fire({
                        title: 'Xóa bác sĩ thành công',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1500
                    }).then((result) => {
                        if (result.dismiss === Swal.DismissReason.timer) {
                            window.location.href = 'http://localhost:8080/admin/doctors';
                        }
                    });
                },
                error: function (xhr, status, error) {
                    console.log(error);
                    Swal.fire({
                        title: 'Đã xảy ra lỗi khi xóa bác sĩ',
                        icon: 'error'
                    });
                }
            });
        }
    });
}