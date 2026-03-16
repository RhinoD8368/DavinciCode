import styles from './Input.module.css';

const Input2 = ({ label, children, ...props}) => {
    return (
        <div className={styles.inputContainer}>
            {label && 
                <label className={styles.label} htmlFor={props.name}>
                    {label}
                </label>
            }
            <input className={styles.inputField} type="text" placeholder={props.placeholder} />
        </div>
    );
}

export default Input2;