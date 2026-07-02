import request, { toFormData } from './request'

export function createShare(resumeId, expiresAt) {
  const params = { resumeId }
  if (expiresAt) params.expiresAt = expiresAt
  return request.post('/hcq/ResumeShare/createShare', null, { params })
}

export function getSharesByResumeId(resumeId) {
  return request.get('/hcq/ResumeShare/getSharesByResumeId', { params: { resumeId } })
}

export function disableShare(id, resumeId) {
  return request.put('/hcq/ResumeShare/disableShare', null, { params: { id, resumeId } })
}

export function enableShare(id, resumeId) {
  return request.put('/hcq/ResumeShare/enableShare', null, { params: { id, resumeId } })
}

export function updateExpiresAt(id, resumeId, expiresAt) {
  return request.put('/hcq/ResumeShare/updateExpiresAt', null, { params: { id, resumeId, expiresAt } })
}

export function deleteShare(id, resumeId) {
  return request.delete('/hcq/ResumeShare/deleteShare', { params: { id, resumeId } })
}

export function viewSharedResume(token) {
  return request.get('/hcq/ResumeShare/public/view', { params: { token } })
}

export function getPublicResume(resumeId) {
  return request.get('/hcq/ResumeShare/public/get', { params: { resumeId } })
}

export function chatWithMemory(content, conversationId, model) {
  const params = { content, conversationId }
  if (model) params.model = model
  return request.post('/hcq/chat/chatWithMemory', toFormData(params), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}
