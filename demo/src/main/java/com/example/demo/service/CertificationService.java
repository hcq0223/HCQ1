package com.example.demo.service;
import com.example.demo.pojo.Certification;
import java.util.List;

public interface CertificationService {

    Boolean addCertification(Certification certification, Integer currentUserId);

    Boolean updateCertification(Certification certification, Integer currentUserId);

    Boolean deleteCertification(Integer id, Integer resumeId, Integer currentUserId);

    Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId);

    Boolean sortCertifications(Integer resumeId, List<Integer> sortedIds, Integer currentUserId);

    List<Certification> getCertificationsByResumeId(Integer resumeId, Integer currentUserId);

    Certification getCertificationById(Integer id, Integer resumeId, Integer currentUserId);
}
