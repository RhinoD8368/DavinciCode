import './App.css'
import { Route, Routes } from 'react-router-dom';
import AuthLayout from './components/layouts/authLayout/AuthLayout'; 
import Login from './pages/loginPage/Login';
import Join from './pages/joinPage/Join';
import Lobby from './pages/lobbyPage/Lobby';

function App() {

  return (
    <Routes> 
      <Route path='/' element={<AuthLayout />} >
        <Route index element={ <Login /> } />
        <Route path="/join" element={ <Join /> } />
        <Route path="/lobby" element={ <Lobby /> } />
      </Route>
    </Routes>
  )
}

export default App
