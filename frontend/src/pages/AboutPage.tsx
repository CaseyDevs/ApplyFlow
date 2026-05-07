import styles from './AboutPage.module.css';

export default function AboutPage() {
  return (
    <main className={styles.container}>
      {/* Header Section */}
      <section className={styles.header}>
        <h1>About ApplyFlow</h1>
        <p className={styles.subtitle}>
          Simplifying job search management for job seekers everywhere
        </p>
      </section>

      {/* Mission Section */}
      <section className={styles.section}>
        <div className={styles.contentWrapper}>
          <h2>Our Mission</h2>
          <p>
            At ApplyFlow, we believe that managing a job search shouldn't be stressful or complicated. 
            Our mission is to provide job seekers with an intuitive, powerful platform that helps them 
            organize their applications, track their progress, and ultimately land their dream job.
          </p>
          <p>
            We've built ApplyFlow with a focus on simplicity and efficiency, eliminating the need for 
            spreadsheets and scattered notes. Everything you need is in one place, designed to help you succeed.
          </p>
        </div>
      </section>

      {/* Features Highlights */}
      <section className={styles.section}>
        <div className={styles.contentWrapper}>
          <h2>What We Offer</h2>
          <div className={styles.highlightGrid}>
            <div className={styles.highlight}>
              <h3>📱 Intuitive Design</h3>
              <p>A clean, modern interface that's easy to use and understand from day one.</p>
            </div>
            <div className={styles.highlight}>
              <h3>🗂️ Smart Organization</h3>
              <p>Organize your applications by job boards, status, date, or any custom category you create.</p>
            </div>
            <div className={styles.highlight}>
              <h3>📊 Real-time Tracking</h3>
              <p>Track application statuses, interview schedules, and follow-ups all in one place.</p>
            </div>
            <div className={styles.highlight}>
              <h3>🔒 Privacy First</h3>
              <p>Your data is secure and private. We never share your information with third parties.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className={styles.section + ' ' + styles.sectionAlt}>
        <div className={styles.contentWrapper}>
          <h2>Our Values</h2>
          <div className={styles.valuesGrid}>
            <div className={styles.valueCard}>
              <h3>Simplicity</h3>
              <p>
                We believe in keeping things simple. No unnecessary features, no overwhelming complexity. 
                Just what you need to succeed.
              </p>
            </div>
            <div className={styles.valueCard}>
              <h3>Reliability</h3>
              <p>
                You can count on ApplyFlow to be there when you need it. We're committed to uptime and 
                consistent performance.
              </p>
            </div>
            <div className={styles.valueCard}>
              <h3>User-Focused</h3>
              <p>
                Every feature we build is informed by our users. Your feedback shapes the future of 
                ApplyFlow.
              </p>
            </div>
            <div className={styles.valueCard}>
              <h3>Transparency</h3>
              <p>
                We're open about what we do, how we do it, and where we're headed. No surprises or 
                hidden agendas.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Get Started Section */}
      <section className={styles.section}>
        <div className={styles.contentWrapper}>
          <div className={styles.cta}>
            <h2>Ready to Streamline Your Job Search?</h2>
            <p>Join ApplyFlow today and experience a better way to manage your applications.</p>
            <button className={styles.ctaButton}>Get Started Free</button>
          </div>
        </div>
      </section>
    </main>
  );
}
