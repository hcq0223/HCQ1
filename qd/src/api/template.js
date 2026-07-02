import request from './request'

export function getAvailableTemplates(category, isPremium) {
  const params = {}
  if (category) params.category = category
  if (isPremium !== undefined) params.isPremium = isPremium
  return request.get('/hcq/ResumeTemplate/getAvailableTemplates', { params })
}