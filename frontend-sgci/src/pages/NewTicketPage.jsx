import { useState, useEffect, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { AuthContext } from '../contexts/AuthContext.jsx'; //

function NewTicketPage() {
    const [titulo, setTitulo] = useState('');
    const [descricao, setDescricao] = useState('');
    const [prioridade, setPrioridade] = useState('MEDIA');
    const [equipamentoId, setEquipamentoId] = useState('');
    const [equipamentos, setEquipamentos] = useState([]);
    const navigate = useNavigate();
    const { isAuthenticated } = useContext(AuthContext); // Pega o status de autenticação

    useEffect(() => {
        const fetchEquipamentos = async () => {
            // Lembrete: Se o usuário não estiver autenticado, não faz sentido buscar.
            if (!isAuthenticated) return;

            try {
                // Lembrete: Não preciso mais mandar o token manualmente, o AuthContext cuida disso.
                const response = await axios.get('http://localhost:8080/equipamentos');
                setEquipamentos(response.data);
                if (response.data.length > 0) {
                    setEquipamentoId(response.data[0].id);
                }
            } catch (error) {
                console.error("Erro ao buscar equipamentos", error);
                alert("Não foi possível carregar a lista de equipamentos.");
            }
        };
        fetchEquipamentos();
    }, [isAuthenticated]); // Roda a busca quando o status de autenticação é definido.

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            const novoChamado = {
                titulo,
                descricao,
                prioridade,
                equipamentoId: Number(equipamentoId)
            };

            await axios.post('http://localhost:8080/chamados', novoChamado);

            alert('Chamado criado com sucesso!');
            navigate('/dashboard');
        } catch (error) {
            console.error("Erro ao criar chamado:", error);
            alert('Falha ao criar chamado. Verifique os dados.');
        }
    };

    return (

        <div className="bg-slate-900 text-white min-h-screen flex items-center justify-center p-4">
            <div className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-lg">
                <h2 className="text-2xl font-bold text-center mb-8">Abrir Novo Chamado</h2>
                <form onSubmit={handleSubmit}>
                    {/* Título */}
                    <div className="mb-4">
                        <label htmlFor="titulo" className="block mb-2 text-sm font-medium">Título</label>
                        <input type="text" id="titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} required
                               className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"/>
                    </div>

                    {/* Equipamento */}
                    <div className="mb-4">
                        <label htmlFor="equipamento" className="block mb-2 text-sm font-medium">Equipamento</label>
                        <select id="equipamento" value={equipamentoId} onChange={(e) => setEquipamentoId(e.target.value)} required
                                className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg">
                            {equipamentos.map(equip => (
                                <option key={equip.id} value={equip.id}>{equip.tag} - {equip.localizacao}</option>
                            ))}
                        </select>
                    </div>

                    {/* Prioridade */}
                    <div className="mb-4">
                        <label htmlFor="prioridade" className="block mb-2 text-sm font-medium">Prioridade</label>
                        <select id="prioridade" value={prioridade} onChange={(e) => setPrioridade(e.target.value)} required
                                className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg">
                            <option value="BAIXA">Baixa</option>
                            <option value="MEDIA">Média</option>
                            <option value="ALTA">Alta</option>
                        </select>
                    </div>

                    {/* Descrição */}
                    <div className="mb-6">
                        <label htmlFor="descricao" className="block mb-2 text-sm font-medium">Descrição do Problema</label>
                        <textarea id="descricao" rows="4" value={descricao} onChange={(e) => setDescricao(e.target.value)} required
                                  className="w-full p-2.5 bg-slate-700 border border-slate-600 rounded-lg"></textarea>
                    </div>

                    <div className="flex items-center gap-4">
                        <button type="submit"
                                className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-2.5 px-4 rounded-lg">
                            Abrir Chamado
                        </button>
                        <Link to="/dashboard"
                              className="w-full text-center bg-gray-500 hover:bg-gray-600 text-white font-bold py-2.5 px-4 rounded-lg">
                            Cancelar
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default NewTicketPage;