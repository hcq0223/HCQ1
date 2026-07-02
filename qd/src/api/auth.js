import request, { toFormData } from './request'

export function login(username, password) {
  return request.post('/hcq/login', toFormData({ username, password }), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function adminLogin(username, password) {
  return request.post('/hcq/admin/login', toFormData({ username, password }), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function register(username, password, email) {
  return request.post('/hcq/register', toFormData({ username, password, email }), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function forgetPassword(email, plainPassword) {
  return request.post('/hcq/forgetPassword', toFormData({ email, plainPassword }), {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function logout() {
  return request.post('/hcq/logout')
}

export function updateUser(user) {
  return request.put('/hcq/update', user)
}