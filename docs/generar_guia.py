# -*- coding: utf-8 -*-
"""
Genera GuiaDiagranaUML.pdf a partir de este script.

El PDF original venia de ReportLab pero sin fuente en el repo, asi que cada
correccion obligaba a rehacerlo a mano. Este archivo ES la fuente: se edita
aqui y se regenera el PDF.

Uso:
    pip install reportlab
    python docs/generar_guia.py

Escribe GuiaDiagranaUML.pdf en la raiz del repo.
"""

import os

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate, Frame, KeepTogether, PageBreak, PageTemplate,
    Paragraph, Spacer, Table, TableStyle,
)

# ---------------------------------------------------------------- fuentes ---
# Arial trae las flechas y simbolos que las fuentes base de ReportLab no cubren.
FONTS = "C:/Windows/Fonts"
try:
    pdfmetrics.registerFont(TTFont("Guia", os.path.join(FONTS, "arial.ttf")))
    pdfmetrics.registerFont(TTFont("Guia-Bold", os.path.join(FONTS, "arialbd.ttf")))
    pdfmetrics.registerFont(TTFont("Guia-Italic", os.path.join(FONTS, "ariali.ttf")))
    pdfmetrics.registerFontFamily("Guia", normal="Guia", bold="Guia-Bold",
                                  italic="Guia-Italic")
    BASE, BOLD = "Guia", "Guia-Bold"
except Exception:
    BASE, BOLD = "Helvetica", "Helvetica-Bold"

# ----------------------------------------------------------------- paleta ---
GRIS_F2 = colors.HexColor("#F2F2F2")
GRIS_BF = colors.HexColor("#BFBFBF")
GRIS_59 = colors.HexColor("#595959")
GRIS_26 = colors.HexColor("#262626")
GRIS_0D = colors.HexColor("#0D0D0D")
AVISO_BG = colors.HexColor("#FBF3E4")
AVISO_LN = colors.HexColor("#C9A227")

ss = getSampleStyleSheet()


def estilo(nombre, **kw):
    kw.setdefault("fontName", BASE)
    kw.setdefault("textColor", GRIS_26)
    return ParagraphStyle(nombre, parent=ss["Normal"], **kw)


E_PORTADA_T = estilo("pt", fontName=BOLD, fontSize=25, leading=30,
                     alignment=TA_CENTER, textColor=GRIS_0D)
E_PORTADA_S = estilo("ps", fontSize=13, leading=18, alignment=TA_CENTER,
                     textColor=GRIS_59)
E_PORTADA_P = estilo("pp", fontSize=9.5, leading=14, alignment=TA_CENTER,
                     textColor=GRIS_59)
E_SECCION = estilo("sec", fontName=BOLD, fontSize=18, leading=22,
                   textColor=GRIS_0D, spaceBefore=4, spaceAfter=9,
                   keepWithNext=1)
E_CLASE = estilo("cls", fontName=BOLD, fontSize=14.5, leading=18,
                 textColor=GRIS_0D, spaceBefore=10, spaceAfter=4,
                 keepWithNext=1)
E_INTRO = estilo("intro", fontSize=8.8, leading=12.5, textColor=GRIS_59,
                 spaceAfter=7, keepWithNext=1)
E_ETIQUETA = estilo("etq", fontName=BOLD, fontSize=8, leading=11,
                    textColor=GRIS_59, spaceBefore=8, spaceAfter=3,
                    keepWithNext=1)
E_CUERPO = estilo("body", fontSize=9, leading=13, spaceAfter=5)
E_REGLA = estilo("regla", fontSize=8.5, leading=12, leftIndent=8, spaceAfter=3)
E_CELDA = estilo("td", fontSize=7.6, leading=9.6)
E_TH = estilo("th", fontName=BOLD, fontSize=7.6, leading=9.6,
              textColor=colors.white)
E_AVISO_T = estilo("avt", fontName=BOLD, fontSize=8.5, leading=11.5,
                   textColor=GRIS_0D)
E_AVISO = estilo("av", fontSize=8.5, leading=11.5, textColor=GRIS_26)

ANCHO = 18.4 * cm


def seccion(txt):
    return [Paragraph(txt, E_SECCION),
            Table([[""]], colWidths=[ANCHO], rowHeights=[2],
                  style=TableStyle([("BACKGROUND", (0, 0), (-1, -1), GRIS_26)])),
            Spacer(1, 9)]


def tabla(encabezados, filas, anchos):
    data = [[Paragraph(h, E_TH) for h in encabezados]]
    for f in filas:
        data.append([Paragraph(str(c), E_CELDA) for c in f])
    t = Table(data, colWidths=anchos, repeatRows=1, hAlign="LEFT")
    estilos = [
        ("BACKGROUND", (0, 0), (-1, 0), GRIS_59),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("GRID", (0, 0), (-1, -1), 0.4, GRIS_BF),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
    ]
    for i in range(1, len(data)):
        if i % 2 == 0:
            estilos.append(("BACKGROUND", (0, i), (-1, i), GRIS_F2))
    t.setStyle(TableStyle(estilos))
    return t


def aviso(titulo, lineas):
    """Recuadro que marca donde el codigo se aparta de la guia original."""
    contenido = [Paragraph(titulo, E_AVISO_T)]
    for ln in lineas:
        contenido.append(Paragraph("&bull; " + ln, E_AVISO))
    inner = Table([[c] for c in contenido], colWidths=[ANCHO - 0.7 * cm],
                  hAlign="LEFT")
    inner.setStyle(TableStyle([
        ("LEFTPADDING", (0, 0), (-1, -1), 0),
        ("RIGHTPADDING", (0, 0), (-1, -1), 0),
        ("TOPPADDING", (0, 0), (-1, -1), 1),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 1),
    ]))
    caja = Table([[inner]], colWidths=[ANCHO], hAlign="LEFT")
    caja.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), AVISO_BG),
        ("LINEBEFORE", (0, 0), (0, -1), 2.5, AVISO_LN),
        ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8),
        ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]))
    return caja


A_ATTR = [3.4 * cm, 2.4 * cm, 2.2 * cm, 1.2 * cm, 2.4 * cm, 6.8 * cm]
A_MET = [6.6 * cm, 4.0 * cm, 7.8 * cm]
H_ATTR = ["Nombre", "Java", "MySQL", "Long.", "Restricción", "Descripción"]
H_MET = ["Firma", "Retorno", "Descripción"]


def clase(nombre, descripcion, atributos, metodos=None, reglas=None,
          relaciones=None, cambios=None):
    fl = [Paragraph(nombre, E_CLASE), Paragraph(descripcion, E_INTRO)]
    if cambios:
        fl += [aviso(cambios[0], cambios[1]), Spacer(1, 8)]
    fl += [Paragraph("ATRIBUTOS", E_ETIQUETA), tabla(H_ATTR, atributos, A_ATTR)]
    if metodos:
        fl += [Paragraph("MÉTODOS &mdash; API de Service/Repository", E_ETIQUETA),
               tabla(H_MET, metodos, A_MET)]
    if reglas:
        bloque = [Paragraph("REGLAS DE NEGOCIO", E_ETIQUETA)]
        bloque += [Paragraph("&rarr; " + r, E_REGLA) for r in reglas]
        fl.append(KeepTogether(bloque))
    if relaciones:
        bloque = [Paragraph("RELACIONES", E_ETIQUETA)]
        bloque += [Paragraph("&bull; " + r, E_REGLA) for r in relaciones]
        fl.append(KeepTogether(bloque))
    fl.append(Spacer(1, 12))
    return fl


def decorado(canvas, doc):
    canvas.saveState()
    if doc.page > 1:
        canvas.setFont(BASE, 7)
        canvas.setFillColor(GRIS_BF)
        canvas.drawString(
            2 * cm, 1.25 * cm,
            "Guía de referencia · Taller de colisión · sincronizada con el código "
            "(2026-09-05)")
        canvas.drawRightString(19.5 * cm, 1.25 * cm, str(doc.page))
        canvas.setStrokeColor(GRIS_F2)
        canvas.setLineWidth(0.5)
        canvas.line(2 * cm, 1.7 * cm, 19.5 * cm, 1.7 * cm)
    canvas.restoreState()


def construir(destino, contenido):
    doc = BaseDocTemplate(
        destino, pagesize=letter,
        leftMargin=2 * cm, rightMargin=1.1 * cm,
        topMargin=1.7 * cm, bottomMargin=2.1 * cm,
        title="Guía de referencia — Sistema de gestión de taller de colisión",
        author="Equipo del proyecto",
        subject="Modelo de datos y reglas de negocio")
    frame = Frame(doc.leftMargin, doc.bottomMargin, ANCHO,
                  letter[1] - doc.topMargin - doc.bottomMargin, id="f")
    doc.addPageTemplates([PageTemplate(id="normal", frames=[frame],
                                       onPage=decorado)])
    doc.build(contenido)


if __name__ == "__main__":
    from contenido_guia import construir_contenido

    raiz = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    salida = os.path.join(raiz, "GuiaDiagranaUML.pdf")
    construir(salida, construir_contenido())
    print("Generado:", salida)
