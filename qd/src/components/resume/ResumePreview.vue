<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatDate, getAvatarUrl, SKILL_LEVELS, LANGUAGE_LEVELS } from '@/utils'
import { getAvailableTemplates } from '@/api/template'

const props = defineProps({
  resume: { type: Object, default: () => ({}) },
  templateCategory: { type: String, default: '' }
})

const templates = ref([])

onMounted(async () => {
  try {
    const { data } = await getAvailableTemplates()
    templates.value = data.templates || []
  } catch {}
})

const templateClass = computed(() => {
  const cat = props.resume?.templateCategory || props.templateCategory || ''
  if (cat) return 'tpl-' + cat
  const id = props.resume?.templateId
  if (!id) return ''
  if (templates.value.length) {
    const match = templates.value.find(t => t.id === id)
    if (match?.category) return 'tpl-' + match.category
  }
  return ''
})
</script>

<template>
  <div class="resume-preview-paper" :class="templateClass">
    <div class="preview-header">
      <div v-if="resume.avatarUrl" class="preview-avatar">
        <img :src="getAvatarUrl(resume.avatarUrl)" alt="avatar" />
      </div>
      <div class="preview-name-block">
        <h1>{{ resume.fullName || '姓名' }}</h1>
        <div class="contact-row">
          <span v-if="resume.email">{{ resume.email }}</span>
          <span v-if="resume.phone">{{ resume.phone }}</span>
          <span v-if="resume.location">{{ resume.location }}</span>
        </div>
        <div class="link-row">
          <span v-if="resume.linkedinUrl">LinkedIn</span>
          <span v-if="resume.githubUrl">GitHub</span>
          <span v-if="resume.personalWebsite">Website</span>
        </div>
      </div>
    </div>

    <div v-if="resume.professionalSummary" class="preview-section">
      <h2>个人简介</h2>
      <p>{{ resume.professionalSummary }}</p>
    </div>

    <div v-if="resume.workExperiences?.length" class="preview-section">
      <h2>工作经历</h2>
      <div v-for="exp in resume.workExperiences" :key="exp.id" class="preview-item">
        <div class="item-header">
          <strong>{{ exp.companyName }}</strong>
          <span class="date">{{ formatDate(exp.startDate) }} - {{ formatDate(exp.endDate) }}</span>
        </div>
        <div class="item-sub">{{ exp.position }}<span v-if="exp.location"> · {{ exp.location }}</span></div>
        <p v-if="exp.description">{{ exp.description }}</p>
        <p v-if="exp.achievements" class="achievement">{{ exp.achievements }}</p>
      </div>
    </div>

    <div v-if="resume.educations?.length" class="preview-section">
      <h2>教育经历</h2>
      <div v-for="edu in resume.educations" :key="edu.id" class="preview-item">
        <div class="item-header">
          <strong>{{ edu.schoolName }}</strong>
          <span class="date">{{ formatDate(edu.startDate) }} - {{ formatDate(edu.endDate) }}</span>
        </div>
        <div class="item-sub">{{ edu.degree }} · {{ edu.major }}<span v-if="edu.gpa"> · GPA {{ edu.gpa }}</span></div>
        <p v-if="edu.description">{{ edu.description }}</p>
      </div>
    </div>

    <div v-if="resume.skills?.length" class="preview-section">
      <h2>技能</h2>
      <div class="skill-tags">
        <span v-for="skill in resume.skills" :key="skill.id" class="skill-tag">
          {{ skill.skillName }} ({{ SKILL_LEVELS[skill.proficiencyLevel] || skill.proficiencyLevel }})
        </span>
      </div>
    </div>

    <div v-if="resume.projects?.length" class="preview-section">
      <h2>项目经历</h2>
      <div v-for="proj in resume.projects" :key="proj.id" class="preview-item">
        <div class="item-header">
          <strong>{{ proj.projectName }}</strong>
          <span class="date">{{ formatDate(proj.startDate) }} - {{ formatDate(proj.endDate) }}</span>
        </div>
        <div class="item-sub">{{ proj.role }}</div>
        <p v-if="proj.description">{{ proj.description }}</p>
        <p v-if="proj.technologiesUsed" class="tech">技术栈：{{ proj.technologiesUsed }}</p>
      </div>
    </div>

    <div v-if="resume.certifications?.length" class="preview-section">
      <h2>证书与奖项</h2>
      <div v-for="cert in resume.certifications" :key="cert.id" class="preview-item">
        <div class="item-header">
          <strong>{{ cert.name }}</strong>
          <span class="date">{{ formatDate(cert.issueDate) }}</span>
        </div>
        <div class="item-sub">{{ cert.issuingOrganization }}</div>
      </div>
    </div>

    <div v-if="resume.languages?.length" class="preview-section">
      <h2>语言能力</h2>
      <div class="skill-tags">
        <span v-for="lang in resume.languages" :key="lang.id" class="skill-tag">
          {{ lang.languageName }} ({{ LANGUAGE_LEVELS[lang.proficiencyLevel] || lang.proficiencyLevel }})
        </span>
      </div>
    </div>

    <div v-if="resume.customSections?.length">
      <div v-for="section in resume.customSections" :key="section.id" class="preview-section">
        <h2>{{ section.sectionTitle }}</h2>
        <div class="custom-content" v-html="section.content"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.resume-preview-paper {
  background: #fff;
  padding: 40px 36px;
  font-size: 13px;
  line-height: 1.6;
  color: #333;
}

.preview-header {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--primary);
}

.preview-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
}

.preview-name-block h1 {
  font-size: 24px;
  color: var(--primary);
  margin-bottom: 6px;
}

.contact-row, .link-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-section h2 {
  font-size: 15px;
  color: var(--primary);
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 4px;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.preview-item {
  margin-bottom: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.item-sub {
  color: #666;
  font-size: 12px;
  margin-bottom: 4px;
}

.date {
  font-size: 11px;
  color: #999;
  white-space: nowrap;
}

.achievement, .tech {
  font-size: 12px;
  color: #555;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.skill-tag {
  background: var(--primary-light);
  color: var(--primary);
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.custom-content {
  font-size: 13px;
}
</style>

<style>
/* Template: Professional */
.tpl-professional .preview-header { border-bottom: 3px solid #2563eb; background: linear-gradient(135deg, #eff6ff 0%, #fff 60%); margin: -24px -28px 20px -28px; padding: 24px 28px; border-radius: 8px 8px 0 0; }
.tpl-professional .preview-name-block h1 { color: #1e40af; font-size: 26px; }
.tpl-professional .preview-section h2 { color: #2563eb; border-bottom: 2px solid #2563eb; padding-bottom: 6px; font-weight: 600; }
.tpl-professional .skill-tag { background: #dbeafe; color: #1e40af; border: none; }
/* Template: Creative */
.tpl-creative { font-family: 'Georgia', serif; background: linear-gradient(135deg, #faf5ff 0%, #fff 100%); }
.tpl-creative .preview-header { border-bottom: none; background: linear-gradient(135deg, #7c3aed, #db2777); margin: -24px -28px 20px -28px; padding: 28px 28px; border-radius: 8px 8px 0 0; }
.tpl-creative .preview-name-block h1 { color: #fff; font-size: 28px; text-shadow: 0 1px 2px rgba(0,0,0,0.1); }
.tpl-creative .contact-row, .tpl-creative .link-row { color: rgba(255,255,255,0.85); }
.tpl-creative .preview-section h2 { color: #7c3aed; border-bottom: 2px dashed #ddd; font-style: italic; text-transform: none; letter-spacing: 1px; }
.tpl-creative .item-header strong { color: #6d28d9; }
.tpl-creative .skill-tag { background: linear-gradient(135deg, #f3e8ff, #fce4ec); color: #7c3aed; border: 1px solid #e9d5ff; }
/* Template: Modern */
.tpl-modern .preview-header { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); padding: 20px 24px; margin: 0 0 16px 0; border-left: 4px solid #0ea5e9; }
.tpl-modern .preview-name-block h1 { color: #0f172a; font-size: 22px; }
.tpl-modern .preview-section { background: #fff; border-radius: 8px; padding: 16px 20px; margin-bottom: 12px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.tpl-modern .preview-section h2 { color: #0ea5e9; border-bottom: 1px solid #f1f5f9; padding-bottom: 8px; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; }
.tpl-modern .item-header strong { color: #334155; }
.tpl-modern .skill-tag { background: #f0f9ff; color: #0284c7; border: 1px solid #bae6fd; border-radius: 6px; }
.tpl-modern .date { font-size: 11px; background: #f1f5f9; padding: 1px 8px; border-radius: 4px; }
/* Template: Minimalist */
.tpl-minimalist .preview-header { border-bottom: 1px solid #e5e7eb; padding-bottom: 12px; margin-bottom: 16px; }
.tpl-minimalist .preview-name-block h1 { color: #111827; font-size: 20px; font-weight: 300; letter-spacing: 2px; text-transform: uppercase; }
.tpl-minimalist .contact-row, .tpl-minimalist .link-row { font-size: 11px; color: #9ca3af; gap: 16px; }
.tpl-minimalist .preview-section h2 { font-size: 11px; color: #d1d5db; border: none; text-transform: uppercase; letter-spacing: 3px; font-weight: 400; margin-bottom: 8px; }
.tpl-minimalist .preview-item { border-bottom: 1px solid #f3f4f6; padding-bottom: 8px; margin-bottom: 8px; }
.tpl-minimalist .item-header strong { font-weight: 500; color: #374151; }
.tpl-minimalist .date { font-size: 10px; color: #d1d5db; }
.tpl-minimalist .item-sub { font-size: 11px; color: #9ca3af; }
.tpl-minimalist .skill-tag { background: #f9fafb; color: #6b7280; border: 1px solid #e5e7eb; border-radius: 2px; padding: 1px 8px; font-size: 11px; }
.tpl-minimalist .preview-section p { color: #6b7280; font-size: 12px; }
/* Template: Academic */
.tpl-academic { font-family: 'Times New Roman', serif; background: #fffdfa; font-size: 12px; }
.tpl-academic .preview-header { text-align: center; border-bottom: 2px solid #1e293b; padding-bottom: 16px; margin-bottom: 20px; }
.tpl-academic .preview-name-block h1 { color: #1e293b; font-size: 22px; font-weight: 700; text-transform: uppercase; letter-spacing: 3px; }
.tpl-academic .contact-row, .tpl-academic .link-row { justify-content: center; font-size: 11px; color: #475569; }
.tpl-academic .preview-section h2 { color: #1e293b; border-bottom: 1px solid #cbd5e1; text-transform: uppercase; letter-spacing: 2px; font-size: 12px; font-weight: 600; }
.tpl-academic .preview-item { margin-bottom: 14px; }
.tpl-academic .item-header strong { font-weight: 700; color: #0f172a; }
.tpl-academic .item-sub { font-style: italic; color: #64748b; }
.tpl-academic .date { color: #94a3b8; font-style: italic; }
.tpl-academic .skill-tag { background: #f1f5f9; color: #334155; border-radius: 2px; font-family: 'Times New Roman', serif; font-size: 11px; }
.tpl-academic .achievement, .tpl-academic .tech { font-size: 11px; font-style: italic; }
</style>