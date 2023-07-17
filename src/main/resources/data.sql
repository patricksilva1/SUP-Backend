CREATE TABLE conta
(
    id_conta IDENTITY NOT NULL PRIMARY KEY,
    nome_responsavel VARCHAR(50) NOT NULL,
    data_de_criacao TIMESTAMP WITH TIME ZONE,
    saldo DECIMAL(20, 2) NOT NULL DEFAULT 0.0
);


CREATE TABLE transferencia
(
    id IDENTITY NOT NULL PRIMARY KEY,
    data_transferencia TIMESTAMP WITH TIME ZONE NOT NULL,
    valor NUMERIC (20,2) NOT NULL,
    tipo VARCHAR(15) NOT NULL,
    nome_operador_transacao VARCHAR (50),
    conta_id INT NOT NULL,

        CONSTRAINT FK_CONTA
        FOREIGN KEY (conta_id)
        REFERENCES conta(id_conta)
);

INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (1, 'Fulano', null, 173.66);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (2, 'Sicrano', null, 956.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (3, 'Patrick', null, 35.78);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (4, 'Marcio', '2023-07-16 09:55:13-3', 4640.24);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (5, 'Rodolfo', '2023-07-16 10:10:25-3', 9958.82);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (6, 'jose', '2023-07-16 10:27:20-3', 181.66);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (7, 'Amarildo', '2023-07-16 15:12:48-3', 8307.10);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (8, 'Roberto', '2023-07-16 16:40:34-3', 1584.61);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (9, 'Roberto Carlos', '2023-07-16 19:48:59-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (10, 'Roberto 2', '2023-07-16 19:53:13-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (11, 'Roberto 2', '2023-07-16 19:53:29-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (12, 'ze 2', '2023-07-16 20:00:24-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (13, 'Marcus', '2023-07-17 11:15:25-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (14, 'Vini', '2023-07-17 11:19:06-3', 0.00);
INSERT INTO conta (id_conta, nome_responsavel, data_de_criacao, saldo) VALUES (15, 'Ceia', '2023-07-17 11:19:46-3', 0.00);


INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (1, '2019-01-01 12:00:00+03', 30895.46, 'DEPOSITO', null, 1);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (2, '2019-02-03 09:53:27+03', 12.24, 'DEPOSITO', null, 2);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (3, '2019-05-04 08:12:45+03', -500.50, 'SAQUE', null, 1);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (4, '2019-08-07 08:12:45+03', -530.50, 'SAQUE', null, 2);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (5, '2020-06-08 10:15:01+03', 3241.23, 'TRANSFERENCIA', 'Beltrano', 1);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (6, '2021-04-01 12:12:04+03', 25173.09, 'TRANSFERENCIA', 'Ronnyscley', 2);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (7, '2023-07-16 10:11:26.894364-03', 13.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (8, '2023-07-16 10:13:52.560626-03', 13.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (9, '2023-07-16 10:14:56.454262-03', 13.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (10, '2023-07-16 10:20:09.448328-03', 13.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (11, '2023-07-16 10:27:47.308106-03', 1.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (12, '2023-07-16 10:31:29-03', 1.78, 'TRANSF_SAIDA', 'Patrick', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (13, '2023-07-16 12:34:11-03', 1.78, 'TRANSF_SAIDA', 'jose', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (14, '2023-07-16 14:49:30.355019-03', -10.00, 'SAQUE', 'Sistema', 5);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (15, '2023-07-16 15:24:24-03', 1.78, 'TRANSF_SAIDA', 'jose', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (16, '2023-07-16 15:25:19-03', 178.10, 'TRANSF_SAIDA', 'Amarildo', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (17, '2023-07-16 15:25:44-03', 178.10, 'TRANSF_ENTRADA', 'Amarildo', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (18, '2023-07-16 15:26:11-03', 178.10, 'TRANSFERENCIA', 'Amarildo', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (19, '2023-07-16 15:26:36-03', 178.10, 'TRANSFERENCIA', 'Marcio', 7);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (20, '2023-07-16 15:27:12-03', 178.10, 'TRANSFERENCIA', 'Patrick', 7);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (21, '2023-07-16 15:27:47-03', 178.10, 'TRANSFERENCIA', 'Amarildo', 3);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (22, '2023-07-16 15:35:41-03', 178.10, 'TRANSFERENCIA', 'Marcio', 7);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (23, '2023-07-16 15:36:05-03', 178.10, 'TRANSFERENCIA', 'jose', 4);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (24, '2023-07-16 16:41:33-03', 155.00, 'TRANSFERENCIA', 'Fulano', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (25, '2023-07-16 16:41:51-03', 478.00, 'TRANSFERENCIA', 'Sicrano', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (26, '2023-07-16 16:48:50-03', 478.00, 'TRANSFERENCIA', 'Sicrano', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (27, '2023-07-16 17:39:55.713502-03', -22.90, 'SAQUE', 'Sistema', 3);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (28, '2023-07-16 22:11:01.427717-03', -2.90, 'SAQUE', 'Sistema', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (29, '2023-07-17 10:46:48.36191-03', -2.90, 'SAQUE', 'Sistema', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (30, '2023-07-17 10:54:46-03', -2.90, 'SAQUE', 'Sistema', 8);
INSERT INTO transferencia (id, data_transferencia, valor, tipo, nome_operador_transacao, conta_id)
VALUES (31, '2023-07-17 11:17:08-03', -2.90, 'SAQUE', 'Sistema', 8);



