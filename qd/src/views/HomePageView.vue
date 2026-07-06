<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const scrolled = ref(false)

function handleScroll() {
  scrolled.value = window.scrollY > 40
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))

function goLogin() { router.push('/login') }
function goRegister() { router.push('/register') }
function goDashboard() { router.push('/dashboard') }
</script>

<template>
  <div class="home-page">
    <!-- Navbar -->
    <header class="home-navbar" :class="{ scrolled }">
      <div class="nav-inner">
        <router-link to="/" class="nav-logo">
          <svg width="28" height="28" viewBox="0 0 32 32" fill="none">
            <rect width="32" height="32" rx="6" fill="#2563eb"/>
            <path d="M8 10h16v2H8V10zm0 5h12v2H8v-2zm0 5h14v2H8v-2z" fill="white"/>
          </svg>
          <span>Resume Builder</span>
        </router-link>
        <div class="nav-actions">
          <button class="btn btn-ghost nav-btn" @click="goLogin">登录</button>
          <button class="btn btn-primary nav-btn" @click="goRegister">注册</button>
        </div>
      </div>
    </header>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">简历更出色，<br />机会更从容</h1>
        <p class="hero-subtitle">
          Resume Builder 帮你快速创建专业简历——从精选模板到成品输出，<br class="hide-mobile" />只需几分钟，轻松拿到心仪面试。
        </p>
        <div class="hero-actions">
          <button class="btn btn-primary hero-cta" @click="goRegister">免费开始使用</button>
          <a href="#features" class="hero-link">了解更多 &darr;</a>
        </div>
      </div>
      <div class="hero-bg" />
    </section>

    <!-- Features -->
    <section id="features" class="features">
      <div class="features-inner">
        <h2 class="section-title">打造专业简历，从未如此简单</h2>
        <p class="section-subtitle">从模板选择到简历输出，每一步都为你优化</p>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon template-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                <line x1="3" y1="9" x2="21" y2="9"/>
                <line x1="9" y1="21" x2="9" y2="9"/>
              </svg>
            </div>
            <h3>精选模板</h3>
            <p>多款专业设计的简历模板，覆盖不同行业与职位，让每一份简历都脱颖而出。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon ai-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 2a4 4 0 014 4c0 2-2 3-4 5-2-2-4-3-4-5a4 4 0 014-4z"/>
                <path d="M12 14a6 6 0 016 6H6a6 6 0 016-6z"/>
              </svg>
            </div>
            <h3>AI 智能助手</h3>
            <p>内置 AI 帮你优化措辞、润色描述，让简历内容更具说服力和专业性。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon preview-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </div>
            <h3>实时预览</h3>
            <p>编辑所见即所得，随时查看简历的最终效果，确保每一处细节都完美无瑕。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon share-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="18" cy="5" r="3"/>
                <circle cx="6" cy="12" r="3"/>
                <circle cx="18" cy="19" r="3"/>
                <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
                <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
              </svg>
            </div>
            <h3>一键分享</h3>
            <p>生成专属分享链接，一键发送给招聘方，支持 PDF 导出，随时投递。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="cta-inner">
        <h2>准备好打造你的下一份简历了吗？</h2>
        <p>免费注册，立即开始。无需信用卡，无隐藏费用。</p>
        <button class="btn btn-primary cta-btn" @click="goRegister">免费开始使用</button>
      </div>
    </section>

    <!-- Footer -->
    <footer class="home-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <svg width="20" height="20" viewBox="0 0 32 32" fill="none">
            <rect width="32" height="32" rx="6" fill="#2563eb"/>
            <path d="M8 10h16v2H8V10zm0 5h12v2H8v-2zm0 5h14v2H8v-2z" fill="white"/>
          </svg>
          <span>Resume Builder</span>
        </div>
        <p class="footer-copy">&copy; 2026 Resume Builder. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #fff;
}

/* ── Navbar ── */
.home-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 0;
  transition: background 0.3s, padding 0.3s, box-shadow 0.3s;
}
.home-navbar.scrolled {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  padding: 10px 0;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.06);
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.nav-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  font-size: 16px;
  color: #fff;
  transition: color 0.3s;
}
.home-navbar.scrolled .nav-logo {
  color: var(--text);
}
.nav-logo:hover {
  opacity: 0.85;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-btn {
  height: 36px;
  padding: 0 18px;
  font-size: 14px;
  border-radius: 8px;
}
.home-navbar:not(.scrolled) .btn-ghost.nav-btn {
  color: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 255, 255, 0.25);
}
.home-navbar:not(.scrolled) .btn-ghost.nav-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.5);
}

/* ── Hero ── */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #1e3a5f 70%, #0f172a 100%);
  z-index: 0;
}
.hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 30% 50%, rgba(37, 99, 235, 0.15) 0%, transparent 60%),
              radial-gradient(ellipse at 70% 50%, rgba(37, 99, 235, 0.08) 0%, transparent 50%);
}
.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 120px 24px 80px;
  max-width: 800px;
}
.hero-title {
  font-size: clamp(36px, 6vw, 56px);
  font-weight: 800;
  line-height: 1.2;
  color: #fff;
  letter-spacing: -0.02em;
  margin-bottom: 20px;
}
.hero-subtitle {
  font-size: clamp(15px, 2vw, 18px);
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.65);
  margin-bottom: 36px;
}
.hide-mobile {
  display: inline;
}
.hero-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}
.hero-cta {
  height: 50px;
  padding: 0 36px;
  font-size: 16px;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.35);
}
.hero-cta:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 24px rgba(37, 99, 235, 0.45);
}
.hero-link {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 2px;
  transition: color 0.2s, border-color 0.2s;
}
.hero-link:hover {
  color: #fff;
  border-color: #fff;
}

/* ── Features ── */
.features {
  padding: 100px 24px;
  background: #fff;
}
.features-inner {
  max-width: 1100px;
  margin: 0 auto;
}
.section-title {
  font-size: clamp(24px, 3.5vw, 36px);
  font-weight: 700;
  text-align: center;
  color: var(--text);
  margin-bottom: 12px;
}
.section-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  text-align: center;
  margin-bottom: 60px;
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}
.feature-card {
  background: #f8fafc;
  border-radius: 14px;
  padding: 36px 28px;
  transition: transform 0.25s, box-shadow 0.25s;
}
.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.06);
}
.feature-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}
.template-icon {
  background: #eff6ff;
  color: #2563eb;
}
.ai-icon {
  background: #f0fdf4;
  color: #16a34a;
}
.preview-icon {
  background: #fefce8;
  color: #ca8a04;
}
.share-icon {
  background: #fef2f2;
  color: #dc2626;
}
.feature-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}
.feature-card p {
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-secondary);
}

/* ── CTA ── */
.cta-section {
  padding: 80px 24px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}
.cta-inner {
  max-width: 640px;
  margin: 0 auto;
  text-align: center;
}
.cta-inner h2 {
  font-size: clamp(22px, 3vw, 32px);
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
}
.cta-inner p {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 28px;
}
.cta-btn {
  height: 50px;
  padding: 0 36px;
  font-size: 16px;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.35);
}
.cta-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 24px rgba(37, 99, 235, 0.45);
}

/* ── Footer ── */
.home-footer {
  padding: 32px 24px;
  background: #0b1120;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 600;
  font-size: 14px;
}
.footer-copy {
  color: rgba(255, 255, 255, 0.35);
  font-size: 13px;
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  .hide-mobile {
    display: none;
  }
  .hero-content {
    padding: 100px 20px 60px;
  }
  .footer-inner {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>
