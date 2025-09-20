import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from './contexts/AuthContext.jsx';



import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import NewTicketPage from './pages/NewTicketPage.jsx';
import ChamadoDetalhePage from './pages/ChamadoDetalhePage.jsx';
import EquipamentosPage from "./pages/EquipamentosPage.jsx";


// Componente para proteger rotas PRIVADAS.
const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useContext(AuthContext);
    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }
    return children;
};

// Componente para proteger rotas PÚBLICAS.
const PublicRoute = ({ children }) => {
    const { isAuthenticated } = useContext(AuthContext);
    if (isAuthenticated) {
        return <Navigate to="/dashboard" />;
    }
    return children;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* === Rotas Públicas === */}
                <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
                <Route path="/registrar" element={<PublicRoute><RegisterPage /></PublicRoute>} />

                {/* === Rotas Privadas === */}
                <Route
                    path="/dashboard"
                    element={
                        <PrivateRoute>
                            <DashboardPage />
                        </PrivateRoute>
                    }
                />
                <Route
                    path="/chamados/novo"
                    element={
                        <PrivateRoute>
                            <NewTicketPage />
                        </PrivateRoute>
                    }
                />

                {/* >> NOVA ROTA ADICIONADA AQUI << */}
                {/* Lembrete: O ':id' é um parâmetro dinâmico. A página vai pegar esse valor da URL. */}
                <Route
                    path="/chamados/:id"
                    element={
                        <PrivateRoute>
                            <ChamadoDetalhePage />
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/equipamentos"
                    element={
                        <PrivateRoute>
                            <EquipamentosPage />
                        </PrivateRoute>
                    }
                />

                {/* Rota Padrão: redireciona qualquer outra URL para o dashboard. */}
                <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;