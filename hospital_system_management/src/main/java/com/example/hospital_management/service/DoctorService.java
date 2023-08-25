package com.example.hospital_management.service;

import com.example.hospital_management.entity.Appointment;
import com.example.hospital_management.entity.Doctor;
import com.example.hospital_management.entity.Speciality;
import com.example.hospital_management.entity.User;
import com.example.hospital_management.exception.NotFoundException;
import com.example.hospital_management.model.request.DoctorSearchRequest;
import com.example.hospital_management.model.request.UpdateDoctorRequest;
import com.example.hospital_management.model.response.CommonResponse;
import com.example.hospital_management.model.response.Doctor2Response;
import com.example.hospital_management.model.response.DoctorResponse;
import com.example.hospital_management.repository.AppointmentRepository;
import com.example.hospital_management.repository.DoctorRepository;
import com.example.hospital_management.repository.SpecialityRepository;
import com.example.hospital_management.repository.UserRepository;
import com.example.hospital_management.statics.DoctorLevel;
import lombok.AllArgsConstructor;
import org.sonatype.sisu.siesta.common.error.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
@AllArgsConstructor
public class DoctorService {

    DoctorRepository doctorRepository;
    final SpecialityRepository specialityRepository;
    UserRepository userRepository;
    AppointmentRepository appointmentRepository;

    public List<Doctor> getAllDoctor() {
        return doctorRepository.findAll();
    }

    public List<DoctorResponse> getAllDoctorResponse() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorResponse> doctorResponses = new ArrayList<>();
        for (Doctor d : doctors) {
            DoctorResponse doctorResponse = DoctorResponse.builder()
                    .id(d.getId())
                    .doctorLevel(d.getDoctorLevel() == null ? "" : d.getDoctorLevel().getName())
                    .dob(d.getDob())
                    .address(d.getAddress())
                    .specialities(d.getSpecialities())
                    .user(d.getUser())
                    .introduce(d.getIntroduce())
                    .specialities(d.getSpecialities())
                    .gender(d.getUser().getGender().getName())
                    .build();
            doctorResponses.add(doctorResponse);
        }
        return doctorResponses;
    }

    public List<DoctorResponse> findBySpecialy(Long idSpecialy) {
        List<Doctor> doctors = doctorRepository.findBySpeciality(idSpecialy);
        List<DoctorResponse> doctorResponses = new ArrayList<>();
        for (Doctor d : doctors) {
            DoctorResponse doctorResponse = DoctorResponse.builder()
                    .id(d.getId())
                    .doctorLevel(d.getDoctorLevel() == null ? "" : d.getDoctorLevel().getName())
                    .dob(d.getDob())
                    .address(d.getAddress())
                    .specialities(d.getSpecialities())
                    .user(d.getUser())
                    .introduce(d.getIntroduce())
                    .specialities(d.getSpecialities())
                    .gender(d.getUser().getGender().getName())
                    .build();
            doctorResponses.add(doctorResponse);
        }
        return doctorResponses;
    }

    public void updateDoctor(Long id, UpdateDoctorRequest registrationRequest) {

        Doctor doctor = doctorRepository.findById(id).orElse(null);
        Set<Speciality> specialities = new LinkedHashSet<>();
        for (Long speciality : registrationRequest.getSpecialityIds()) {
            specialities.add(specialityRepository.findById(speciality).orElse(null));
        }
        if (doctor != null) {
            User user = doctor.getUser();
            userRepository.save(user);

            doctor.setUser(user);
            doctor.setPhone(registrationRequest.getPhone());
//            doctor.setDoctorLevel(registrationRequest.getDoctorLevel());
            for (DoctorLevel s : DoctorLevel.values()) {
                System.out.println("s: "+s.getName());
                System.out.println("s val: "+s.toString());
                System.out.println("s vals: "+s);
                System.out.println("request: "+registrationRequest.getLevel());
                if (s.toString().equals(registrationRequest.getLevel())){
                    doctor.setDoctorLevel(s);
                }
            }
            doctor.setAddress(registrationRequest.getAddress());
            doctor.setIntroduce(registrationRequest.getIntroduce());
            doctor.setSpecialities(specialities);
            doctorRepository.save(doctor);
        }
    }

    public void deleteDoctor(Long id) throws NotFoundException {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found doctor"));
        doctorRepository.delete(doctor);
    }

//    public void deleteDoctor1(Long id) throws BadRequestException {
//        Optional<Doctor> doctorOptional = doctorRepository.findById(id);
//        if (doctorOptional.isEmpty()) {
//            throw new NotFoundException("Không tìm thấy bác sĩ");
//        }
//        List<Appointment> appointmentList = appointmentRepository.findAllBySpecialities(doctorOptional.get());
//        if (appointmentList.size() > 0) {
//            throw new BadRequestException("Không thể xóa vì bác sĩ đã có cuộc hẹn");
//        }
//        doctorRepository.delete(longValue(id));
//    }
//
//    public void deleteSpeciality(Long id) throws BadRequestException {
//        Optional<Speciality> specialityOptional = specialityRepository.findById(id);
//        if (specialityOptional.isEmpty()) {
//            throw new NotFoundException("Không tìm thấy chuyên khoa");
//        }
//        List<Doctor> doctorList = doctorRepository.findAllBySpecialities(specialityOptional.get());
//        if (doctorList.size() > 0) {
//            throw new BadRequestException("Không thể xóa vì đang có bác sĩ trong chuyên khoa này");
//        }
//        specialityRepository.deleteById(id);
//    }


    public Doctor findById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public CommonResponse<?> searchDoctor(DoctorSearchRequest request) {
       return null;
    }

    public Page<Doctor> findAllByAdmin(String param,Long idspecialy, Pageable pageable){
        Page<Doctor> page = null;
        if(idspecialy != null){
            page = doctorRepository.searchDoctorBySpecialy("%"+param+"%", idspecialy, pageable);
        }
        else{
            page = doctorRepository.searchDoctorByParam("%"+param+"%", pageable);
        }
        return page;
    }
}
