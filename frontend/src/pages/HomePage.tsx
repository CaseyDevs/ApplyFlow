import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from './HomePage.module.css';

export default function HomePage() {
  const { isLoggedIn } = useAuth();
  const navigate = useNavigate();

  return (
    <main className={styles.container}>
      {/* Hero Section */}
      <section className={styles.hero}>
        <div className={styles.heroContent}>
          <h1 className={styles.title}>
            Manage Your <span className={styles.highlight}>Job Applications</span> Effortlessly
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
      <section className={styles.features}>
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
