import { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext.jsx';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const { login } = useContext(AuthContext);

    const handleSubmit = async (event) => {
        event.preventDefault();
        const success = await login(email, password);

        if (success) {
            navigate('/dashboard');
        } else {
            alert('Falha no login. Verifique as credenciais.');
        }
    };

    return (
        <div className="bg-slate-900 text-white min-h-screen flex items-center justify-center">
            <div className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-8">Login - SGCI</h2>

                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="email" className="block mb-2 text-sm font-medium">Email</label>
                        <input
                            type="email"
                            id="email"
                            className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label htmlFor="password" className="block mb-2 text-sm font-medium">Senha</label>
                        <input
                            type="password"
                            id="password"
                            className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2.5 px-4 rounded-lg"
                    >
                        Entrar
                    </button>
                </form>


                <p className="text-center text-sm text-slate-400 mt-6">
                    Não tem uma conta?{' '}
                    <Link to="/registrar" className="font-medium text-blue-400 hover:underline">
                        Registre-se
                    </Link>
                </p>

            </div>
        </div>
    );
}

export default LoginPage;