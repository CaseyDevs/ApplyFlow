import styles from "../pages/InvitationConfirmationPage.module.css"

interface DetailItemProps {
    label: string;
    value: string;
}

export default function DetailItem({ label, value }: DetailItemProps) {
    return (
        <div className={styles.detailSection}>
            <p className={styles.detailLabel}>{label}</p>
            <p className={styles.detailValue}>{value}</p>
        </div>
    );
}