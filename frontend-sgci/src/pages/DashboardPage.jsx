import { useContext, useState, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext.jsx';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function DashboardPage() {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const [chamados, setChamados] = useState([]);
    const [loading, setLoading] = useState(true);


    // >> Logica de paginação <<

    //  Estado para guardar as infos da paginação que vêm do backend.
    const [paginaInfo, setPaginaInfo] = useState(null);
    //  Estado para controlar em qual página estamos. Começa na página 0.
    const [paginaAtual, setPaginaAtual] = useState(0);

    //  A função de busca agora pede uma página específica.
    const fetchChamados = async (paginaNumero) => {
        setLoading(true);
        try {
            // A URL agora inclui o parâmetro '?page=' para pedir a página correta.
            const response = await axios.get(`http://localhost:8080/chamados?page=${paginaNumero}&size=10&sort=dataAbertura,desc`);

            setChamados(response.data.content); // A lista de chamados da página.
            setPaginaInfo(response.data); // O objeto completo com infos de paginação.
        } catch (error) {
            console.error("Erro ao buscar chamados:", error);
            if (error.response && (error.response.status === 401 || error.response.status === 403)) {
                logout();
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    // : O useEffect agora depende da 'paginaAtual'.
    // Toda vez que a 'paginaAtual' mudar (ao clicar nos botões), ele vai buscar os dados de novo.
    useEffect(() => {
        if (user) {
            fetchChamados(paginaAtual);
        }
    }, [user, paginaAtual]);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    // : Funções para navegar entre as páginas.
    const handlePaginaAnterior = () => {
        if (paginaInfo && !paginaInfo.first) {
            setPaginaAtual(paginaAtual - 1);
        }
    };

    const handleProximaPagina = () => {
        if (paginaInfo && !paginaInfo.last) {
            setPaginaAtual(paginaAtual + 1);
        }
    };

    const isSolicitante = user?.role === 'ROLE_SOLICITANTE';

    return (
        <div className="bg-slate-900 text-white min-h-screen p-8">
            <header className="flex justify-between items-center mb-12">
                <div>
                    <h1 className="text-3xl font-bold">Dashboard de Chamados</h1>
                    {user && <p className="text-slate-400">Logado como: {user.email} ({user.role})</p>}
                </div>
                <button
                    onClick={handleLogout}
                    className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-lg"
                >
                    Sair
                </button>
            </header>

            <main>
                {loading ? (
                    <p className="text-center text-lg">Carregando chamados...</p>
                ) : (
                    <div className="bg-slate-800 rounded-lg shadow-lg p-6">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-semibold">
                                {isSolicitante ? 'Meus Chamados Recentes' : 'Fila de Chamados em Aberto'}
                            </h2>

                            {isSolicitante && (
                                <Link to="/chamados/novo" className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-lg transition-colors">
                                    Criar Novo Chamado
                                </Link>
                            )}

                            {user?.role === 'ROLE_ADMIN' && (
                                <Link to="/equipamentos" className="bg-purple-600 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded-lg">
                                    Gerenciar Equipamentos
                                </Link>
                            )}
                        </div>

                        {chamados.length > 0 ? (
                            <>
                                <ul className="divide-y divide-slate-700">
                                    {chamados.map(chamado => (
                                        <Link key={chamado.id} to={`/chamados/${chamado.id}`}>
                                            <li className="py-4 px-2 flex justify-between items-center hover:bg-slate-700 rounded-md transition-colors">
                                                <div>
                                                    <p className="font-bold text-lg">{chamado.titulo}</p>
                                                    {!isSolicitante && <p className="text-xs text-slate-400">Aberto por: {chamado.nomeSolicitante}</p>}
                                                    <p className="text-sm text-slate-300 mt-1">{chamado.descricao.substring(0, 100)}...</p>
                                                </div>
                                                <span className="bg-blue-500 text-xs font-semibold px-2.5 py-1 rounded-full">{chamado.status}</span>
                                            </li>
                                        </Link>
                                    ))}
                                </ul>


                                {/* >> controles de paginação << */}

                                {paginaInfo && paginaInfo.totalPages > 1 && (
                                    <div className="flex justify-between items-center mt-6 pt-4 border-t border-slate-700">
                                        <button
                                            onClick={handlePaginaAnterior}
                                            disabled={paginaInfo.first}
                                            className="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            Anterior
                                        </button>
                                        <span className="text-slate-400">
                                            Página {paginaInfo.number + 1} de {paginaInfo.totalPages}
                                        </span>
                                        <button
                                            onClick={handleProximaPagina}
                                            disabled={paginaInfo.last}
                                            className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            Próxima
                                        </button>
                                    </div>
                                )}
                            </>
                        ) : (
                            <p className="text-center text-slate-400">{isSolicitante ? 'Você ainda não abriu nenhum chamado.' : 'Não há chamados na fila.'}</p>
                        )}
                    </div>
                )}
            </main>
        </div>
    );
}

export default DashboardPage;