import styles from './Input.module.css';

const Input = ({label, validation, ...props}) => {
    return(
        <div className={styles.inputContainer}>
            {label && <label className={styles.label} htmlFor={props.name}>{label}</label>}
            <input
                className={styles.inputField}
                {...props}
            />
            { 
                validation?.showText
                && <p style={{color:validation.color, fontWeight:"bold"}}>{validation.message}</p> 
            }
            
        </div>
    );
}

export default Input;