import { useState, useEffect, useContext } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { AuthContext } from '../contexts/AuthContext';

function ChamadoDetalhePage() {
    // Aqui eu pego o ID do chamado da URL (ex: /chamados/1).
    const { id } = useParams();
    const { user } = useContext(AuthContext);
    const navigate = useNavigate();

    // Aqui eu crio os estados para guardar os dados do chamado e controlar a tela.
    const [chamado, setChamado] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Aqui eu crio os estados para controlar os modais (pop-ups) e o texto dos inputs.
    const [isAtualizacaoModalOpen, setIsAtualizacaoModalOpen] = useState(false);
    const [isFecharModalOpen, setIsFecharModalOpen] = useState(false);
    const [textoAtualizacao, setTextoAtualizacao] = useState('');
    const [notaResolucao, setNotaResolucao] = useState('');

    // Aqui eu crio a função que busca os dados do chamado na API quando a página carrega.
    const fetchChamado = async () => {
        try {
            setLoading(true); // Começo a mostrar o "Carregando..."
            const response = await axios.get(`http://localhost:8080/chamados/${id}`);
            setChamado(response.data); // Guardo os dados do chamado no estado.
        } catch (err) {
            setError("Erro ao carregar o chamado.");
            console.error(err);
        } finally {
            setLoading(false); // Paro de mostrar o "Carregando...".
        }
    };

    // Aqui eu uso o useEffect para chamar a função 'fetchChamado' assim que a página renderiza.
    useEffect(() => {
        fetchChamado();
    }, [id]);

    // Aqui eu fiz a lógica para o botão "Atribuir a mim".
    const handleAtribuir = async () => {
        try {
            const response = await axios.put(`http://localhost:8080/chamados/${id}/atribuir`);
            setChamado(response.data); // Atualizo a tela com a resposta da API em tempo real.
        } catch (err) {
            alert("Falha ao atribuir chamado: " + (err.response?.data?.message || err.message));
        }
    };

    // Aqui eu fiz a lógica para o formulário do modal "Adicionar Atualização".
    const handleAdicionarAtualizacao = async (e) => {
        e.preventDefault(); // Impede o recarregamento da página
        try {
            const response = await axios.post(`http://localhost:8080/chamados/${id}/atualizacoes`, { descricao: textoAtualizacao });
            setChamado(response.data); // Atualizo a tela com o chamado que contém a nova atualização.
            setTextoAtualizacao(''); // Limpo o campo de texto.
            setIsAtualizacaoModalOpen(false); // Fecho o modal.
        } catch (err) {
            alert("Falha ao adicionar atualização: " + (err.response?.data?.message || err.message));
        }
    };

    // Aqui eu fiz a lógica para o formulário do modal "Fechar Chamado".
    const handleFecharChamado = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.put(`http://localhost:8080/chamados/${id}/fechar`, { notaDeResolucao: notaResolucao });
            setChamado(response.data); // Atualizo a tela com o status "CONCLUIDO".
            setNotaResolucao('');
            setIsFecharModalOpen(false);
        } catch (err) {
            alert("Falha ao fechar chamado: " + (err.response?.data?.message || err.message));
        }
    };

    // Aqui eu coloco um retorno antecipado para os casos de carregamento e erro.
    if (loading) return <div className="bg-slate-900 text-white min-h-screen flex items-center justify-center"><p>Carregando...</p></div>;
    if (error) return <div className="bg-slate-900 text-white min-h-screen flex items-center justify-center"><p className="text-red-500">{error}</p></div>;
    if (!chamado) return null; // Garante que o código abaixo não quebre se o chamado for nulo.

    return (
        <div className="bg-slate-900 text-white min-h-screen p-8">
            <nav className="mb-8">
                <Link to="/dashboard" className="text-blue-400 hover:underline">{"< Voltar para o Dashboard"}</Link>
            </nav>

            <div className="bg-slate-800 rounded-lg shadow-lg p-8 max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
                <div className="md:col-span-2">
                    <h1 className="text-3xl font-bold mb-2">{chamado.titulo}</h1>
                    <p className="text-slate-400 mb-6">Aberto por: {chamado.nomeSolicitante}</p>
                    <div className="border-t border-slate-700 mt-4 pt-4">
                        <h2 className="text-xl font-semibold mb-2">Descrição Completa</h2>
                        <p className="text-slate-300 whitespace-pre-wrap">{chamado.descricao}</p>
                    </div>
                    <div className="border-t border-slate-700 mt-6 pt-6">
                        <h2 className="text-xl font-semibold mb-4">Histórico de Atualizações</h2>
                        {chamado.atualizacoes && chamado.atualizacoes.length > 0 ? (
                            <ul className="space-y-4">
                                {/* Aqui eu faço o loop para mostrar cada atualização do histórico. */}
                                {chamado.atualizacoes.map(att => (
                                    <li key={att.id} className="border-l-2 border-blue-500 pl-4">
                                        <p className="text-slate-300">{att.descricao}</p>
                                        <p className="text-xs text-slate-500 mt-1">Por: {att.autor.nome} em {new Date(att.data).toLocaleString()}</p>
                                    </li>
                                ))}
                            </ul>
                        ) : (<p className="text-slate-400">Nenhuma atualização registrada.</p>)}
                    </div>
                </div>
                <div className="bg-slate-900 p-6 rounded-lg self-start">
                    <h2 className="text-xl font-semibold mb-4">Status do Chamado</h2>
                    <ul>
                        <li className="flex justify-between items-center mb-2"><span>Status:</span><span className="bg-blue-500 text-xs font-semibold px-2.5 py-1 rounded-full">{chamado.status}</span></li>
                        <li className="flex justify-between items-center mb-2"><span>Prioridade:</span><span>{chamado.prioridade}</span></li>
                        <li className="flex justify-between items-center mb-2"><span>Equipamento:</span><span>{chamado.tagEquipamento}</span></li>
                        <li className="flex justify-between items-center"><span>Técnico:</span><span>{chamado.tecnico ? chamado.tecnico.nome : "Não atribuído"}</span></li>
                    </ul>

                    {/* Aqui eu só mostro a seção de ações se o usuário for um técnico. */}
                    {user?.role === 'ROLE_TECNICO' && (
                        <div className="border-t border-slate-700 mt-6 pt-6">
                            <h3 className="text-lg font-semibold mb-4">Ações Disponíveis</h3>
                            <div className="flex flex-col gap-4">
                                {/* Aqui eu só mostro o botão "Atribuir a mim" se o chamado estiver ABERTO e sem técnico. */}
                                {!chamado.tecnico && chamado.status === 'ABERTO' && (
                                    <button onClick={handleAtribuir} className="bg-green-600 hover:bg-green-700 w-full px-4 py-2 rounded-lg">Assumir Chamado</button>
                                )}
                                {/* Aqui eu só mostro os outros botões se um técnico já estiver atribuído. */}
                                {chamado.tecnico && (
                                    <>
                                        <button onClick={() => setIsAtualizacaoModalOpen(true)} className="bg-yellow-600 hover:bg-yellow-700 w-full px-4 py-2 rounded-lg">Adicionar Atualização</button>
                                        {/* E o botão de fechar só aparece se o chamado estiver EM_ANDAMENTO. */}
                                        {chamado.status === 'EM_ANDAMENTO' && (
                                            <button onClick={() => setIsFecharModalOpen(true)} className="bg-red-600 hover:bg-red-700 w-full px-4 py-2 rounded-lg">Fechar Chamado</button>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Aqui eu defino o Modal para Adicionar Atualização. Ele só aparece se 'isAtualizacaoModalOpen' for true. */}
            {isAtualizacaoModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
                    <form onSubmit={handleAdicionarAtualizacao} className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                        <h3 className="text-xl font-bold mb-4">Adicionar Atualização</h3>
                        <textarea value={textoAtualizacao} onChange={(e) => setTextoAtualizacao(e.target.value)} rows="4" className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg" required placeholder="Descreva o que foi feito..."></textarea>
                        <div className="flex justify-end gap-4 mt-6">
                            <button type="button" onClick={() => setIsAtualizacaoModalOpen(false)} className="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded-lg">Cancelar</button>
                            <button type="submit" className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-lg">Salvar</button>
                        </div>
                    </form>
                </div>
            )}

            {/* Aqui eu defino o Modal para Fechar o Chamado. Ele só aparece se 'isFecharModalOpen' for true. */}
            {isFecharModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
                    <form onSubmit={handleFecharChamado} className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                        <h3 className="text-xl font-bold mb-4">Fechar Chamado</h3>
                        <textarea value={notaResolucao} onChange={(e) => setNotaResolucao(e.target.value)} rows="4" className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg" placeholder="Descreva a solução aplicada..." required></textarea>
                        <div className="flex justify-end gap-4 mt-6">
                            <button type="button" onClick={() => setIsFecharModalOpen(false)} className="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded-lg">Cancelar</button>
                            <button type="submit" className="bg-red-600 hover:bg-red-700 px-4 py-2 rounded-lg">Confirmar Fechamento</button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}

export default ChamadoDetalhePage;