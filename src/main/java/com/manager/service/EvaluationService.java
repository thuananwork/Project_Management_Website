package com.manager.service;
import com.manager.DTO.EvaluationDTO;
import com.manager.DTO.GradeDetailDTO;
import com.manager.DTO.GradeStudent;
import com.manager.repository.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EvaluationService {
    @Autowired
    private EvaluationRepository evaluationRepository;

    public List<EvaluationDTO> getAllWeeklyProject() {
        List<Object[]> results = evaluationRepository.findAllProject();
        List<EvaluationDTO> evaluationDTOS = new ArrayList<>();

        for (Object[] result : results) {
            EvaluationDTO custom = new EvaluationDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (String) result[3],
                    (String) result[4],
                    (String) result[5],
                    (Date) result[6],
                    (Date) result[7],
                    (String) result[8],
                    (String) result[9],
                    (Double) result[10],
                    (Integer) result[11]
            );
            evaluationDTOS.add(custom);
        }
        return evaluationDTOS;
    }

    public List<EvaluationDTO> getAllWeeklyProjects(Long id) {
        List<Object[]> results = evaluationRepository.findAllProjects(id);
        List<EvaluationDTO> evaluationDTOS = new ArrayList<>();

        for (Object[] result : results) {
            EvaluationDTO custom = new EvaluationDTO(
                    ((Number) result[0]).longValue(),
                    (String) result[1],
                    (Date) result[2],
                    (String) result[3],
                    (String) result[4],
                    (String) result[5],
                    (Date) result[6],
                    (Date) result[7],
                    (String) result[8],
                    (String) result[9],
                    (Double) result[10],
                    (Integer) result[11]
            );
            evaluationDTOS.add(custom);
        }
        return evaluationDTOS;
    }

    public List<GradeDetailDTO> evaluationList(Long id, Long userID){
        List<Object[]> objects = evaluationRepository.findByProjectAndStudent(id, userID);
        List<GradeDetailDTO> gradeDetailDTOS = new ArrayList<>();

        for (Object[] result : objects) {
            GradeDetailDTO custom = new GradeDetailDTO(
                    (String) result[0],
                    (String) result[1],
                    (String) result[2],
                    (BigDecimal) result[3],
                    (Date) result[4],
                    (String) result[5],
                    (String) result[6]
            );
            gradeDetailDTOS.add(custom);
        }
        return gradeDetailDTOS;
    }
    public List<GradeStudent> getAllGradeProject(Long id) {
        List<Object[]> results = evaluationRepository.findAllEvaluation(id);
        List<GradeStudent> gradeStudents = new ArrayList<>();

        for (Object[] result : results) {
            GradeStudent custom = new GradeStudent(
                    (String) result[0],
                    (String) result[1],
                    (Date) result[2],
                    (Date) result[3],
                    (String) result[4],
                    (BigDecimal) result[5],
                    (String) result[6],
                    (String) result[7]
            );
            gradeStudents.add(custom);
        }
        return gradeStudents;
    }

}
