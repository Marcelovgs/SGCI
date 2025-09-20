import { createContext, useState, useEffect } from 'react';
import axios from 'axios';

export const AuthContext = createContext({});

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);

    useEffect(() => {
        // Carrega os dados do localStorage ao iniciar a aplicação
        const token = localStorage.getItem('authToken');
        const email = localStorage.getItem('userEmail');
        const role = localStorage.getItem('userRole');

        if (token && email && role) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            setUser({ email, role });
        }
    }, []);

    const login = async (email, password) => {
        try {
            const response = await axios.post('http://localhost:8080/auth/login', { email, password });
            const { token, role } = response.data;

            // Salva os dados no localStorage
            localStorage.setItem('authToken', token);
            localStorage.setItem('userEmail', email);
            localStorage.setItem('userRole', role);

            // Configura o token padrão para todas as requisições Axios
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

            // Atualiza o estado do usuário
            setUser({ email, role });

            return true;
        } catch (error) {
            console.error("Falha no login", error);
            logout(); // Limpa qualquer dado antigo
            return false;
        }
    };

    const logout = () => {
        // Remove todos os dados do usuário
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userRole');

        delete axios.defaults.headers.common['Authorization'];
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated: !!user, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}
