import { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { AuthContext } from '../contexts/AuthContext.jsx';

function EquipamentosPage() {
    const { user } = useContext(AuthContext);

    const [equipamentos, setEquipamentos] = useState([]);
    const [tag, setTag] = useState('');
    const [tipo, setTipo] = useState('');
    const [localizacao, setLocalizacao] = useState('');
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editingEquipamento, setEditingEquipamento] = useState(null);

    // Função para buscar equipamentos com token
    const fetchEquipamentos = async () => {
        if (!user) return;
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get('http://localhost:8080/equipamentos', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setEquipamentos(response.data);
        } catch (error) {
            console.error("Erro ao buscar equipamentos", error);
            alert("Não foi possível carregar os equipamentos.");
        }
    };

    useEffect(() => {
        fetchEquipamentos();
    }, [user]);

    // Cadastro de novo equipamento
    const handleCadastro = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.post(
                'http://localhost:8080/equipamentos',
                { tag, tipo, localizacao },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setEquipamentos(prev => [response.data, ...prev]);
            setTag('');
            setTipo('');
            setLocalizacao('');
            alert('Equipamento cadastrado com sucesso!');
        } catch (error) {
            console.error("Erro ao cadastrar equipamento", error);
            if (error.response && error.response.status === 403) {
                alert("Erro: você precisa ser ADMIN para cadastrar equipamentos.");
            } else {
                alert('Erro ao cadastrar equipamento. A TAG pode já existir ou você não tem permissão.');
            }
        }
    };

    // Excluir equipamento
    const handleExcluir = async (id) => {
        if (!window.confirm("Tem certeza que deseja excluir este equipamento?")) return;

        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                alert("Você não está autenticado.");
                return;
            }

            await axios.delete(`http://localhost:8080/equipamentos/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            setEquipamentos(prev => prev.filter(eq => eq.id !== id));
            alert("Equipamento excluído com sucesso.");
        } catch (error) {
            console.error("Erro ao excluir equipamento", error);
            if (error.response && error.response.status === 403) {
                alert("Não foi possivel excluir, a camera tem um chamado em aberto.");
            } else {
                alert("Erro ao excluir equipamento. Tente novamente.");
            }
        }
    };

    // Abrir modal de edição
    const openEditModal = (equipamento) => {
        setEditingEquipamento(equipamento);
        setIsEditModalOpen(true);
    };

    // Atualizar valores do modal
    const handleEditFormChange = (e) => {
        setEditingEquipamento({ ...editingEquipamento, [e.target.name]: e.target.value });
    };

    // Salvar edição
    const handleEditar = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.put(
                `http://localhost:8080/equipamentos/${editingEquipamento.id}`,
                {
                    tag: editingEquipamento.tag,
                    tipo: editingEquipamento.tipo,
                    localizacao: editingEquipamento.localizacao
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setEquipamentos(prev => prev.map(eq => eq.id === editingEquipamento.id ? response.data : eq));
            setIsEditModalOpen(false);
            alert("Equipamento atualizado com sucesso.");
        } catch (error) {
            console.error("Erro ao atualizar equipamento", error);
            if (error.response && error.response.status === 403) {
                alert("Erro: você precisa ser ADMIN para atualizar este equipamento.");
            } else {
                alert("Erro ao atualizar equipamento. Tente novamente.");
            }
        }
    };

    // Funções de rolagem
    const scrollToTop = () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const scrollToBottom = () => {
        window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
    };

    return (
        <div className="bg-slate-900 text-white min-h-screen p-8">
            <nav className="mb-8">
                <Link to="/dashboard" className="text-blue-400 hover:underline">
                    {"< Voltar para o Dashboard"}
                </Link>
            </nav>

            <div className="max-w-4xl mx-auto">
                <h1 className="text-3xl font-bold mb-6">Gerenciamento de Equipamentos</h1>

                {/* Formulário de cadastro */}
                <form onSubmit={handleCadastro} className="bg-slate-800 p-6 rounded-lg mb-8 grid grid-cols-1 md:grid-cols-4 gap-4 items-center">
                    <input type="text" value={tag} onChange={e => setTag(e.target.value)} placeholder="TAG (ex: CAM-001)" required className="md:col-span-1 bg-slate-700 p-2 rounded h-10"/>
                    <input type="text" value={tipo} onChange={e => setTipo(e.target.value)} placeholder="Tipo (ex: Câmera Dome)" required className="md:col-span-1 bg-slate-700 p-2 rounded h-10"/>
                    <input type="text" value={localizacao} onChange={e => setLocalizacao(e.target.value)} placeholder="Localização" required className="md:col-span-1 bg-slate-700 p-2 rounded h-10"/>
                    <button type="submit" className="md:col-span-1 bg-blue-600 hover:bg-blue-700 p-2 rounded h-10 font-bold">Adicionar</button>
                </form>

                {/* Lista de equipamentos */}
                <div className="bg-slate-800 p-6 rounded-lg">
                    <h2 className="text-xl font-semibold mb-4">Equipamentos Cadastrados</h2>
                    <ul className="divide-y divide-slate-700">
                        {equipamentos.map(eq => (
                            <li key={eq.id} className="py-3 flex justify-between items-center">
                                <div>
                                    <p className="font-bold">{eq.tag}</p>
                                    <p className="text-sm text-slate-400">{eq.tipo} - {eq.localizacao}</p>
                                </div>
                                <div className="flex gap-4">
                                    <button onClick={() => openEditModal(eq)} className="bg-yellow-600 hover:bg-yellow-700 text-white font-bold py-1 px-3 rounded">Editar</button>
                                    <button onClick={() => handleExcluir(eq.id)} className="bg-red-600 hover:bg-red-700 text-white font-bold py-1 px-3 rounded">Excluir</button>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {/* Botões flutuantes */}
            <div className="fixed bottom-6 right-6 flex flex-col gap-3 z-50">

                <button
                    onClick={scrollToTop}
                    className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-full shadow-lg transition-transform hover:scale-105"
                    title="Voltar ao topo"
                >
                    ↑
                </button>

                <button
                    onClick={scrollToBottom}
                    className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full shadow-lg transition-transform hover:scale-105"
                    title="Descer até o fim"
                >
                    ↓
                </button>
            </div>

            {/* Modal de Edição */}
            {isEditModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
                    <form onSubmit={handleEditar} className="bg-slate-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                        <h3 className="text-xl font-bold mb-6">Editando Equipamento: {editingEquipamento.tag}</h3>

                        <div className="mb-4">
                            <label className="block mb-2 text-sm font-medium">Tag</label>
                            <input type="text" name="tag" value={editingEquipamento.tag} onChange={handleEditFormChange} required className="w-full bg-slate-700 p-2 rounded"/>
                        </div>
                        <div className="mb-4">
                            <label className="block mb-2 text-sm font-medium">Tipo</label>
                            <input type="text" name="tipo" value={editingEquipamento.tipo} onChange={handleEditFormChange} required className="w-full bg-slate-700 p-2 rounded"/>
                        </div>
                        <div className="mb-6">
                            <label className="block mb-2 text-sm font-medium">Localização</label>
                            <input type="text" name="localizacao" value={editingEquipamento.localizacao} onChange={handleEditFormChange} required className="w-full bg-slate-700 p-2 rounded"/>
                        </div>

                        <div className="flex justify-end gap-4 mt-8">
                            <button type="button" onClick={() => setIsEditModalOpen(false)} className="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded-lg">Cancelar</button>
                            <button type="submit" className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-lg">Salvar Alterações</button>
                        </div>
                    </form>
                </div>
            )}
        </div>
    );
}

export default EquipamentosPage;
