import request, { toFormData } from './request'

export function selectResumeByUserId() {
  return request.get('/hcq/Resume/selectResumeByUserId')
}

export function selectResumeByTemplateId(templateId) {
  return request.get('/hcq/Resume/selectResumeByTemplateId', { params: { templateId } })
}

export function selectResumeByStatus(status) {
  return request.get('/hcq/Resume/selectResumeByStatus', { params: { status } })
}

export function insertResume(resume) {
  return request.post('/hcq/Resume/insertResume', resume)
}

export function updateResume(resume) {
  return request.put('/hcq/Resume/updateResume', resume)
}

export function updateResumeWithAvatar(resume, avatarFile) {
  const formData = new FormData()
  formData.append('resume', JSON.stringify(resume))
  if (avatarFile) {
    formData.append('avatar', avatarFile)
  }
  return request.put('/hcq/Resume/updateResumeWithAvatar', formData)
}

export function isDeleteResume(id) {
  return request.put('/hcq/Resume/isDeleteResume', null, { params: { id } })
}

export function noIsDeleteResume(id) {
  return request.put('/hcq/Resume/noIsDeleteResume', null, { params: { id } })
}

export function updateStatusResume(id, status) {
  return request.put('/hcq/Resume/updateStatusResume', null, { params: { id, status } })
}

export function selectTrashResume() {
  return request.get('/hcq/Resume/selectTrashResume')
}

export function deleteResume(id) {
  return request.delete('/hcq/Resume/deleteResume', { params: { id } })
}

export function getFullResume(resumeId) {
  return request.get('/hcq/Resume/getFullResume', { params: { resumeId } })
}
