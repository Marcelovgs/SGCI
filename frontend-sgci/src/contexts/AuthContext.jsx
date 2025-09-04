import { createContext, useState, useEffect } from 'react';
import axios from 'axios';

// Crio o contexto que será compartilhado
export const AuthContext = createContext({});

// Crio o "provedor" do contexto. É um componente que vai encapsular a aplicação
export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    // Função de login
    const login = async (email, password) => {
        try {
            const response = await axios.post('http://localhost:8080/auth/login', { email, password });
            const { token } = response.data;

            // Salvo o token no localStorage
            localStorage.setItem('authToken', token);

            // Defini o token no header padrão do Axios para futuras requisições
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

            // Aqui no futuro buscaremos os dados do usuário com o token
            setUser({ email }); // Por enquanto, salvamos um usuário simples

            return true; // Sinaliza que o login deu certo
        } catch (error) {
            console.error("Falha no login", error);
            return false; // Sinaliza que o login falhou
        }
    };

    const logout = () => {
        // Limpo tudo
        localStorage.removeItem('authToken');
        delete axios.defaults.headers.common['Authorization'];
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated: !!user, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}