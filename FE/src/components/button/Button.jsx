import styles from './Button.module.css';

/**
 * @author: 김동민
 * @description: Button 컴포넌트는 재사용 가능한 버튼 요소를 제공합니다.
 * @param {'lg' | 'md' | 'sm'} size 버튼의 크기를 지정하는 속성으로 :root에 정의된 값을 사용합니다. (기본값: 'md')
 * @param {boolean} isWeb 웹 환경여부를 나타내는 속성으로, 모바일과 웹에서 다른 스타일을 적용할 때 사용됩니다. (기본값: true)
 * @param {ReactNode} children - 버튼 내부에 표시될 내용
 * @param {object} props - 추가적인 HTML 속성 (예: onClick, disabled 등)
 * @returns {JSX.Element} 스타일이 적용된 버튼 요소
 */
const Button = ({ isWeb = true, size = 'md', children, ...props }) => {
    
    const btnClassName = `${styles.btn} ${styles[size]}`;

    return (
        <button 
            className={btnClassName}
            {...props}
        >
            {children}
        </button>
    );
};

export default Button;