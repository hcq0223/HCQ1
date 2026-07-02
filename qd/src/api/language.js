import request from './request'

export function selectLanguageByResumeId(resumeId) {
  return request.get('/hcq/Language/selectLanguageByResumeId', { params: { resumeId } })
}

export function insertLanguage(language) {
  return request.post('/hcq/Language/insertLanguage', language)
}

export function updateLanguage(language) {
  return request.put('/hcq/Language/updateLanguage', language)
}

export function deleteLanguage(id, resumeId) {
  return request.delete('/hcq/Language/deleteLanguage', { params: { id, resumeId } })
}

export function languageBatch(resumeId, ids) {
  return request.delete(`/hcq/Language/${resumeId}/languageBatch`, { data: { ids } })
}

export function languageSort(resumeId, sortedIds) {
  return request.put(`/hcq/Language/${resumeId}/languageSort`, sortedIds)
}
