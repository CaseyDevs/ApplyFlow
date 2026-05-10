import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from './HomePage.module.css';

export default function HomePage() {
  const { isLoggedIn, user } = useAuth();
  const navigate = useNavigate();

  return (
    <main className={styles.container}>
      {/* Hero Section */}
      <section className={styles.hero} id="home">
        <div className={styles.heroContent}>
          <h1 className={styles.title}>
            {isLoggedIn ? (
              <>Hi, {user?.name}</>
            ) : (
            <>Manage Your <span className={styles.titleHighlight}>Job Applications</span> Effortlessly</>
            )}
          </h1>
          <p className={styles.subtitle}>
            Track your job applications, manage job boards with friends, and stay organized throughout your job search journey. ApplyFlow makes it simple to succeed.
          </p>
          <div className={styles.ctaButtons}>
            {isLoggedIn ? (
              <>
                <button
                  className={styles.primaryButton}
                  onClick={() => navigate('/job-boards')}
                >
                  View Job Boards
                </button>
                <button
                  className={styles.secondaryButton}
                  onClick={() => navigate('/applications')}
                >
                  View Applications
                </button>
              </>
            ) : (
              <>
                <button
                  className={styles.primaryButton}
                  onClick={() => navigate('/login')}
                >
                  Get Started
                </button>
                <button
                  className={styles.secondaryButton}
                  onClick={() => navigate('/register')}
                >
                  Create Account
                </button>
              </>
            )}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className={styles.features} id="features">
        <h2>Why ApplyFlow?</h2>
        <div className={styles.featureGrid}>
          <div className={styles.featureCard}>
            <div className={styles.featureIcon}>📊</div>
            <h3>Track Applications</h3>
            <p>Keep track of all your job applications in one centralized location with detailed status updates.</p>
          </div>
          <div className={styles.featureCard}>
            <div className={styles.featureIcon}>📌</div>
            <h3>Organize Job Boards</h3>
            <p>Create and manage multiple job boards to organize opportunities by company, role, or status.</p>
          </div>
          <div className={styles.featureCard}>
            <div className={styles.featureIcon}>⚡</div>
            <h3>Stay Efficient</h3>
            <p>Streamline your job search with intuitive tools designed for maximum productivity and clarity.</p>
          </div>
          <div className={styles.featureCard}>
            <div className={styles.featureIcon}>🎯</div>
            <h3>Goal Tracking</h3>
            <p>Set goals and monitor your progress throughout your job search journey with real-time insights.</p>
          </div>
        </div>
      </section>

      {/* What We Offer Section */}
      <section className={styles.offers} id="offers">
        <div className={styles.contentWrapper}>
          <h2>What We Offer</h2>
          <div className={styles.highlightGrid}>
            <div className={styles.highlight}>
              <div className={styles.highlightIcon}>📱</div>
              <h3>Intuitive Design</h3>
              <p>A clean, modern interface that's easy to use and understand from day one.</p>
            </div>
            <div className={styles.highlight}>
              <div className={styles.highlightIcon}>🗂️</div>
              <h3>Smart Organization</h3>
              <p>Organize your applications by job boards, status, date, or any custom category you create.</p>
            </div>
            <div className={styles.highlight}>
              <div className={styles.highlightIcon}>📊</div>
              <h3>Real-time Tracking</h3>
              <p>Track application statuses, interview schedules, and follow-ups all in one place.</p>
            </div>
            <div className={styles.highlight}>
              <div className={styles.highlightIcon}>🔒</div>
              <h3>Privacy First</h3>
              <p>Your data is secure and private. We never share your information with third parties.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Mission Section */}
      <section className={styles.mission} id="mission">
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

      {/* Values Section */}
      <section className={styles.values} id="values">
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

      {/* CTA Section */}
      {!isLoggedIn && (
        <section className={styles.ctaSection}>
          <h2>Ready to Transform Your Job Search?</h2>
          <p>Join hundreds of job seekers using ApplyFlow to stay organized and successful.</p>
          <button
            className={styles.primaryButton}
            onClick={() => navigate('/register')}
          >
            Sign Up Free
          </button>
        </section>
      )}
    </main>
  );
}
