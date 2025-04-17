
import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import TheRoutes from './pages/TheRoutes';

function App() {
  return (
    <Router>
      <TheRoutes />
    </Router>
  );
}

export default App;
