import request from './request'

export function selectCertificationsByResumeId(resumeId) {
  return request.get('/hcq/Certification/selectCertificationsByResumeId', { params: { resumeId } })
}

export function insertCertification(certification, resumeId) {
  return request.post('/hcq/Certification/insertCertification', certification, { params: { resumeId } })
}

export function updateCertification(certification) {
  return request.put('/hcq/Certification/updateCertification', certification)
}

export function deleteCertification(id, resumeId) {
  return request.delete('/hcq/Certification/deleteCertification', { params: { id, resumeId } })
}

export function certificationBatch(resumeId, ids) {
  return request.delete(`/hcq/Certification/${resumeId}/certificationBatch`, { data: { ids } })
}

export function certificationSort(resumeId, sortedIds) {
  return request.put(`/hcq/Certification/${resumeId}/certificationSort`, sortedIds)
}
