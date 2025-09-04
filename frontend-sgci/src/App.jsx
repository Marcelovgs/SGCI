import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from './contexts/AuthContext.jsx';
import LoginPage from "./LoginPage.jsx";
import DashboardPage from "./ pages/DashboardPage.jsx";

// Crio um componente especial para proteger rotas.
const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useContext(AuthContext);

    // Se o usuário não estiver autenticado, redireciono para a página de login.
    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    // Se estiver autenticado, mostro a página que ele tentou acessar.
    return children;
};


function App() {
    return (
        // O BrowserRouter é o que ativa o sistema de rotas.
        <BrowserRouter>
            {/* O Routes é onde eu defino cada rota individualmente. */}
            <Routes>
                {/* Rota pública para a página de login. */}
                <Route path="/login" element={<LoginPage />} />

                {/* Rota protegida para o dashboard. */}
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <DashboardPage />
                        </PrivateRoute>
                    }
                />

                {/* Rota padrão: se o usuário entrar no site, redireciono para o login. */}
                <Route path="*" element={<Navigate to="/login" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;