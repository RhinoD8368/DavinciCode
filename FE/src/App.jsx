import './App.css'

import { Route, Routes } from 'react-router-dom';
import Button from './components/button/Button';

function App() {

  return (
    <Routes>
      <Route path='/' element={<Button/>}>

      </Route>
    </Routes>
  )
}

export default App
