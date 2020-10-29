

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.0
-- Dumped by pg_dump version 9.4.0
-- Started on 2015-02-04 11:11:03 CET


DROP DATABASE "FatturaPA";
--
-- TOC entry 2285 (class 1262 OID 24724)
-- Name: FatturaPA; Type: DATABASE; Schema: -; Owner: luigi
--

CREATE DATABASE "FatturaPA"  ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE "FatturaPA" OWNER TO luigi;

\connect "FatturaPA"




--
-- TOC entry 172 (class 1259 OID 24725)
-- Name: codifica_stati; Type: TABLE; Schema: public; Owner: luigi
--

CREATE TABLE codifica_stati (
    cod_stato bigint NOT NULL,
    desc_stato character varying(255) NOT NULL
);


ALTER TABLE codifica_stati OWNER TO luigi;

--
-- TOC entry 174 (class 1259 OID 24732)
-- Name: dati_fattura; Type: TABLE; Schema: public; Owner: luigi
--

CREATE TABLE dati_fattura (
    id_dati_fattura bigint NOT NULL,
    cedente_id_fiscale_iva character varying(255) NOT NULL,
    committente_id_fiscale_iva character varying(255) NOT NULL,
    data_fattura date NOT NULL,
    identificativo_sdi bigint NOT NULL,
    numero_fattura character varying(255) NOT NULL,
    numero_protocollo character varying(255),
    posizione_fattura integer NOT NULL
);


ALTER TABLE dati_fattura OWNER TO luigi;

--
-- TOC entry 173 (class 1259 OID 24730)
-- Name: dati_fattura_id_dati_fattura_seq; Type: SEQUENCE; Schema: public; Owner: luigi
--

CREATE SEQUENCE dati_fattura_id_dati_fattura_seq
    START WITH 1
    INCREMENT BY 1
   	MINVALUE 1 
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dati_fattura_id_dati_fattura_seq OWNER TO luigi;

--
-- TOC entry 2289 (class 0 OID 0)
-- Dependencies: 173
-- Name: dati_fattura_id_dati_fattura_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: luigi
--

ALTER SEQUENCE dati_fattura_id_dati_fattura_seq OWNED BY dati_fattura.id_dati_fattura;


--
-- TOC entry 175 (class 1259 OID 24743)
-- Name: metadati_fattura; Type: TABLE; Schema: public; Owner: luigi
--

CREATE TABLE metadati_fattura (
    identificativo_sdi bigint NOT NULL,
    codice_destinatario character varying(255),
    data_ricezione_sdi timestamp without time zone,
    formato character varying(255),
    message_id character varying(255),
    nome_file character varying(255),
    note character varying(255),
    tentativi_invio bigint
);


ALTER TABLE metadati_fattura OWNER TO luigi;

--
-- TOC entry 176 (class 1259 OID 24751)
-- Name: stato_fattura; Type: TABLE; Schema: public; Owner: luigi
--

CREATE TABLE stato_fattura (
    id_dati_fattura bigint NOT NULL,
    id_cod_stato bigint NOT NULL,
    data timestamp without time zone
);


ALTER TABLE stato_fattura OWNER TO luigi;

--
-- TOC entry 2161 (class 2604 OID 24735)
-- Name: id_dati_fattura; Type: DEFAULT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY dati_fattura ALTER COLUMN id_dati_fattura SET DEFAULT nextval('dati_fattura_id_dati_fattura_seq'::regclass);


--
-- TOC entry 2163 (class 2606 OID 24729)
-- Name: codifica_stati_pkey; Type: CONSTRAINT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY codifica_stati
    ADD CONSTRAINT codifica_stati_pkey PRIMARY KEY (cod_stato);


--
-- TOC entry 2165 (class 2606 OID 24740)
-- Name: dati_fattura_pkey; Type: CONSTRAINT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY dati_fattura
    ADD CONSTRAINT dati_fattura_pkey PRIMARY KEY (id_dati_fattura);


--
-- TOC entry 2169 (class 2606 OID 24750)
-- Name: metadati_fattura_pkey; Type: CONSTRAINT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY metadati_fattura
    ADD CONSTRAINT metadati_fattura_pkey PRIMARY KEY (identificativo_sdi);


--
-- TOC entry 2171 (class 2606 OID 24755)
-- Name: stato_fattura_pkey; Type: CONSTRAINT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY stato_fattura
    ADD CONSTRAINT stato_fattura_pkey PRIMARY KEY (id_dati_fattura, id_cod_stato);


--
-- TOC entry 2167 (class 2606 OID 24742)
-- Name: u_dt_fttr_identificativo_sdi; Type: CONSTRAINT; Schema: public; Owner: luigi
--

ALTER TABLE ONLY dati_fattura
    ADD CONSTRAINT u_dt_fttr_identificativo_sdi UNIQUE (identificativo_sdi, numero_fattura, data_fattura);



-- Completed on 2015-02-04 11:11:03 CET


