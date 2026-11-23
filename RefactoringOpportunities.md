# Refactoring Opportunities

| Local | Cheiro no código | Refabricação | Nº de aluno |
|---|---|---|---|
| ScoreboardManager::registerWin | Long Method (pesquisa do jogador embutida no método) | Extract Method | 129819 |
| ExportadorPDF::escreverTabuleiro | Primitive Obsession (literais `'#'`, `'*'`, `'o'`, `'.'`) | Introduce Constant | 129819 |
| ExportadorPDF::descreverTiro | Duplicated Code (`resultado.ship().getCategory()` repetido) | Introduce Variable | 129819 |
| Game::getRemainingShips | Variável redundante (usada uma só vez) | Inline Variable | 129819 |
| Fleet::colisionRisk | Nome incorreto (erro ortográfico "colision") | Rename | 129819 |
| Game::randomEnemyFire | Long Method | Extract Method | 129824 |
| Game (campos `count*`) | `Integer` onde bastava `int` | Type Migration | 129824 |
| Game::printBoardShots | Expressão `map[row][col]` repetida | Introduce Variable | 129824 |
| Game::jsonShots e Move::processEnemyFire | Duplicated Code (`ObjectMapper` configurado duas vezes) | Introduce Field | 129824 |
| Game::printBoard / printShips | Nomes fora da convenção Java (`show_shots`, `ship_pos`, `adjacent_pos`) | Rename | 129824 |
| Game::over / randomEnemyFire | Strings literais embutidas no código | Introduce Constant | 129824 |
| Tasks::menu (casos RAJADA e SIMULA) | Duplicated Code (fim de jogo repetido) | Extract Method | 129831 |
| Tasks::readClassicPosition | Nomes pouco claros (`part1`, `part2`) | Rename | 129831 |
| Tasks::menu (caso SIMULA) | Magic Number (3000 em `Thread.sleep`) | Introduce Constant | 129831 |
| Fleet::addShip | Expressão condicional complexa | Introduce Variable | 129831 |
| Tasks::readShip | Variável temporária redundante (`bearing`) | Inline Variable | 129831 |
| Fleet::printShips | Method May Be Static (não usa atributos de instância) | Make Static | 129831 |
| Game::readEnemyFire | Long Method | Extract Method | por atribuir |
| Move::processEnemyFire | Long Method (CC 37) | Extract Method | 129839 |
| Move::processEnemyFire | Duplicated Code (pluralização `> 1 ? "s" : ""`) | Extract Method | por atribuir |
| ExportadorPDF::escreverTabuleiro | Long Method (duplica `Game.printBoard`) | Extract Method | por atribuir |
| Ship (campo `positions`) | Data Class / campo `protected` exposto às subclasses | Encapsulate Fields | 129839 |
| IShip::getSize / Ship::getSize | `Integer` onde bastava `int` (Primitive Obsession) | Change Signature | 129839 |
| Caravel (construtor) | Long Method (switch com 4 ciclos quase iguais) | Extract Method | 129839 |
| Caravel::fillLinearPositions | Duplicated Code (mesma lógica em Caravel, Carrack e Frigate) | Pull Members Up | 129839 |
| Carrack e Frigate (construtores) | Duplicated Code | Find and Replace Code Duplicates | 129839 |
| GameSession::markAiWins / markStudentWins e GameController | Strings literais `"AI_WINS"` / `"STUDENT_WINS"` repetidas | Introduce Constant | 129839 |
| AiShotStrategy::getHitButNotSunkPositions | Message Chains (`move.getShotResults()` repetido 3 vezes) | Introduce Variable | 129839 |
