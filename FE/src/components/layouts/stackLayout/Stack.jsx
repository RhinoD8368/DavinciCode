const Stack = ({ children, direction, gap, style }) => (
  <div style={{
    display: 'flex',
    flexDirection: direction,
    gap: gap,
    ...style
  }}>
    {children}
  </div>
);

/** 
 * @author rhinod
 * @description 세로로 컴포넌트를 쌓습니다.(Vertical Stack) 
 * @param {React.ReactNode} children - Stack 컴포넌트의 자식 요소들
 * @param {string} gap - Stack 요소들 사이의 간격을 지정하는 문자열
 * @param {object} style - Stack 컴포넌트에 추가로 적용할 스타일 객체
 * */
export const VStack = (props) => <Stack {...props} direction="column" />;

/** 
 * @author rhinod
 * @description 가로로 컴포넌트를 쌓습니다.(Horizontal Stack)
 * @param {React.ReactNode} children - Stack 컴포넌트의 자식 요소들
 * @param {string} gap - Stack 요소들 사이의 간격을 지정하는 문자열
 * @param {object} style - Stack 컴포넌트에 추가로 적용할 스타일 객체
 * */
export const HStack = (props) => <Stack {...props} direction="row" />;