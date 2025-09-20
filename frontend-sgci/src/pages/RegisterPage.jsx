// src/pages/RegisterPage.jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function RegisterPage() {
    const [nome, setNome] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            // Faz a chamada para o endpoint público de registro
            await axios.post('http://localhost:8080/auth/registrar', { nome, email, password });

            alert('Usuário cadastrado com sucesso!');
            // Redireciona para a página de login após o sucesso
            navigate('/login');
        } catch (error) {
            console.error("Erro no registro:", error);
            alert('Erro ao registrar. Verifique os dados ou o email pode já estar em uso.');
        }
    };

    return (
        <div className="bg-slate-900 text-white min-h-screen flex items-center justify-center">
            <div className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-8">Criar Conta - SGCI</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="nome" className="block mb-2 text-sm font-medium">Nome Completo</label>
                        <input type="text" id="nome" value={nome} onChange={(e) => setNome(e.target.value)} required
                               className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"/>
                    </div>
                    <div className="mb-4">
                        <label htmlFor="email" className="block mb-2 text-sm font-medium">Email</label>
                        <input type="email" id="email" value={email} onChange={(e) => setEmail(e.target.value)} required
                               className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"/>
                    </div>
                    <div className="mb-6">
                        <label htmlFor="password" className="block mb-2 text-sm font-medium">Senha</label>
                        <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} required
                               className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"/>
                    </div>
                    <button type="submit"
                            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2.5 px-4 rounded-lg">
                        Registrar
                    </button>
                </form>
                <p className="text-center text-sm text-slate-400 mt-6">
                    Já tem uma conta? <Link to="/login" className="text-blue-400 hover:underline">Faça o login</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;