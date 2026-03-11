import './App.css'
import Button from './components/Button/Button'

function App() {

  return (
    <>
      <div style={{ 
        backgroundColor: 'yellow',
        margin: '20px auto',
        padding: '20px',
        width: '500px',
        height: '500px',
       }}>
        <Button isWeb/>
      </div>
    </>
  )
}

export default App
