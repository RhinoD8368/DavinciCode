import { Outlet } from 'react-router-dom';
import styles from './AuthLayout.module.css';
import Header from '../../header/Header';

/* 
* 3단계 레이아웃
* 1. 헤더 영역 : 전체 넓이
* 2. 메인 영역 : 가운데 영역
* 3. 푸터 영역 : 전체 넓이
*/
const AuthLayout = () => {
    return (
        <div className={styles.authLayoutContainer}>
            <div className={styles.headerContainer}>
                <Header />
            </div>
            <div className={styles.authContainer}>
                <Outlet />
            </div>
            <div className={styles.footerContainer}>
                {/* <Footer /> */}
            </div>
        </div>
    )
}

export default AuthLayout;