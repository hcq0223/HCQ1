package com.example.demo.service.impl;

import com.example.demo.mapper.CertificationMapper;
import com.example.demo.mapper.CertificationMapper.SortItem;
import com.example.demo.mapper.ResumeMapper;
import com.example.demo.pojo.Certification;
import com.example.demo.pojo.Resume;
import com.example.demo.service.CertificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {
    private final CertificationMapper certificationMapper;
    private final ResumeMapper resumeMapper;

    public CertificationServiceImpl(CertificationMapper a, ResumeMapper b) {
        this.certificationMapper = a;
        this.resumeMapper = b;
    }

    @Override
    @Transactional
    public Boolean addCertification(Certification certification, Integer currentUserId) {
        if (certification == null || certification.getResumeId() == null || !StringUtils.hasText(certification.getName()))
            return false;
        Resume resume = resumeMapper.selectResumeById(certification.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return false;
        if (certification.getIssueDate() != null && certification.getExpiryDate() != null && certification.getExpiryDate().isBefore(certification.getIssueDate()))
            return false;
        List<Integer> existIds = certificationMapper.selectIdsByResumeId(certification.getResumeId());
        certification.setSortOrder(existIds.size());
        return certificationMapper.insertCertification(certification) > 0;
    }

    @Override
    @Transactional
    public Boolean updateCertification(Certification certification, Integer currentUserId) {
        if (certification == null || certification.getId() == null || certification.getResumeId() == null) return false;
        Resume resume = resumeMapper.selectResumeById(certification.getResumeId());
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return false;
        Certification exist = certificationMapper.selectById(certification.getId(), certification.getResumeId());
        if (exist == null) return false;
        if (certification.getIssueDate() != null && certification.getExpiryDate() != null && certification.getExpiryDate().isBefore(certification.getIssueDate()))
            return false;
        if (certification.getName() == null && certification.getIssuingOrganization() == null && certification.getIssueDate() == null && certification.getExpiryDate() == null && certification.getCredentialId() == null && certification.getCredentialUrl() == null && certification.getSortOrder() == null)
            return false;
        return certificationMapper.updateCertification(certification) > 0;
    }

    @Override
    @Transactional
    public Boolean deleteCertification(Integer id, Integer resumeId, Integer currentUserId) {
        if (id == null || resumeId == null) return false;
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return false;
        return certificationMapper.deleteById(id, resumeId) > 0;
    }

    @Override
    @Transactional
    public Boolean deleteBatch(Integer resumeId, List<Integer> ids, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return false;
        return certificationMapper.deleteCertificationBatch(ids, resumeId, currentUserId) == ids.size();
    }

    @Override
    @Transactional
    public Boolean sortCertifications(Integer resumeId, List<Integer> sortedIds, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return false;
        List<Integer> existIds = certificationMapper.selectIdsByResumeId(resumeId);
        if (existIds.size() != sortedIds.size()) return false;
        List<SortItem> items = new ArrayList<>();
        for (int i = 0; i < sortedIds.size(); i++) items.add(new SortItem(sortedIds.get(i), i));
        return certificationMapper.updateCertificationSortOrderBatch(resumeId, items) == sortedIds.size();
    }

    @Override
    public List<Certification> getCertificationsByResumeId(Integer resumeId, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return java.util.Collections.emptyList();
        return certificationMapper.selectByResumeId(resumeId);
    }

    @Override
    public Certification getCertificationById(Integer id, Integer resumeId, Integer currentUserId) {
        Resume resume = resumeMapper.selectResumeById(resumeId);
        if (resume == null || Boolean.TRUE.equals(resume.getIsDeleted()) || !resume.getUserId().equals(currentUserId))
            return null;
        return certificationMapper.selectById(id, resumeId);
    }
}