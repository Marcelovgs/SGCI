import { useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext.jsx';

// Lembrete para mim mesmo: DashboardPage.jsx
// Objetivo: Mostrar uma mensagem de boas-vindas e um botão de logout.

function DashboardPage() {
    // Pego as informações do usuário e a função de logout do nosso contexto.
    const { user, logout } = useContext(AuthContext);

    return (
        <div className="bg-slate-900 text-white min-h-screen flex flex-col items-center justify-center">
            <h1 className="text-4xl font-bold mb-4">Bem-vindo ao Dashboard!</h1>
            {/* Mostra o email do usuário logado, se existir */}
            {user && <p className="text-lg mb-8">Você está logado como: {user.email}</p>}
            <button
                onClick={logout} // O botão chama a função de logout do contexto.
                className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg"
            >
                Sair (Logout)
            </button>
        </div>
    );
}

export default DashboardPage;