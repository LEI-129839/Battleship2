# Refactoring Opportunities

| Local | Cheiro no código | Refabricação | Nº de aluno |
|---|---|---|---|
| ScoreboardManager::registerWin | Long Method (pesquisa do jogador embutida no método) | Extract Method | 129819 |
| ExportadorPDF::escreverTabuleiro | Primitive Obsession (literais `'#'`, `'*'`, `'o'`, `'.'`) | Introduce Constant | 129819 |
| ExportadorPDF::descreverTiro | Duplicated Code (`resultado.ship().getCategory()` repetido) | Introduce Variable | 129819 |
| Game::getRemainingShips | Variável redundante (usada uma só vez) | Inline Variable | 129819 |
| Fleet::colisionRisk | Nome incorreto (erro ortográfico "colision") | Rename | 129819 |
| Tasks::menu | Long Method | Extract Method | por atribuir |
| Tasks::menu (casos RAJADA e SIMULA) | Duplicated Code (fim de jogo repetido) | Extract Method | por atribuir |
| Tasks::readClassicPosition | Nomes pouco claros (`part1`, `part2`) | Rename | por atribuir |
| Game::randomEnemyFire | Long Method | Extract Method | por atribuir |
| Game::randomEnemyFire | Duplicated Code (dois ciclos `while` semelhantes) | Extract Method | por atribuir |
| Game::readEnemyFire | Long Method | Extract Method | por atribuir |
| Game (campos `count*`) | `Integer` onde bastava `int` | Type Migration | por atribuir |
| Game::printBoardShots | Expressão `map[row][col]` repetida | Introduce Variable | por atribuir |
| Game::jsonShots e Move::processEnemyFire | Duplicated Code (`ObjectMapper` configurado duas vezes) | Introduce Field | por atribuir |
| Move::processEnemyFire | Long Method (CC 37) | Extract Method | por atribuir |
| Move::processEnemyFire | Duplicated Code (pluralização `> 1 ? "s" : ""`) | Extract Method | por atribuir |
| ExportadorPDF::escreverTabuleiro | Long Method (duplica `Game.printBoard`) | Extract Method | por atribuir |
