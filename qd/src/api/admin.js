import request from './request'

export function getAdminUsers() {
  return request.get('/hcq/admin/users')
}

export function getAdminResumes() {
  return request.get('/hcq/admin/resumes')
}

export function getAdminStats() {
  return request.get('/hcq/admin/stats')
}

export function getAdminTemplates() {
  return request.get('/hcq/ResumeTemplate/getTemplateList')
}

export function insertAdminTemplate(template) {
  return request.post('/hcq/ResumeTemplate/insertTemplate', template)
}

export function updateAdminTemplate(template) {
  return request.put('/hcq/ResumeTemplate/updateTemplate', template)
}

export function deleteAdminTemplate(id) {
  return request.delete('/hcq/ResumeTemplate/deleteTemplate', { params: { id } })
}

export function uploadTemplateImage(formData) {
  return request.post('/hcq/ResumeTemplate/uploadPreviewImage', formData)
}

export function toggleAdminTemplate(id, isActive) {
  return request.put('/hcq/ResumeTemplate/toggleActiveStatus', null, { params: { id, isActive } })
}