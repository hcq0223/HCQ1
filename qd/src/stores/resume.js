import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getFullResume } from '@/api/resume'

export const useResumeStore = defineStore('resume', () => {
  const currentResume = ref(null)
  const resumeDetail = ref(null)

  function setCurrentResume(resume) {
    currentResume.value = resume
  }

  async function loadResumeDetail(resumeId) {
    const { data } = await getFullResume(resumeId)
    if (data.resume) {
      resumeDetail.value = data.resume
    }
    return data.resume
  }

  function updateResumeDetail(partial) {
    if (resumeDetail.value) {
      resumeDetail.value = { ...resumeDetail.value, ...partial }
    }
  }

  function clearResumeDetail() {
    currentResume.value = null
    resumeDetail.value = null
  }

  return {
    currentResume,
    resumeDetail,
    setCurrentResume,
    loadResumeDetail,
    updateResumeDetail,
    clearResumeDetail
  }
})
