import './App.css'
import { Route, Routes } from 'react-router-dom';
import AuthLayout from './components/layouts/authLayout/AuthLayout'; 
import Login from './pages/loginPage/Login';
function App() {

  return (
    <Routes> 
      <Route path='/' element={<AuthLayout />} >
        <Route index element={ <Login /> } />
      </Route>
    </Routes>
  )
}

export default App
