<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:regexp="http://exslt.org/regular-expressions"
    extension-element-prefixes="regexp">
    <xsl:output method="text" omit-xml-declaration="yes"
        indent="no" />

    <xsl:template match="/type">
        <xsl:text>package org.philippides.data;&#xa;</xsl:text>
        <xsl:text>&#xa;</xsl:text>
        <xsl:text>import java.io.IOException;&#xa;</xsl:text>
        <xsl:text>import java.io.InputStream;&#xa;</xsl:text>
        <xsl:text>import java.io.OutputStream;&#xa;</xsl:text>
        <xsl:text>import java.util.Iterator;&#xa;</xsl:text>
        <xsl:text>&#xa;</xsl:text>
        <xsl:text>public class </xsl:text>
        <xsl:call-template name="className">
            <xsl:with-param name="name" select="@name" />
        </xsl:call-template>
        <xsl:text> extends Described</xsl:text>
        <xsl:if test="@provides">
            <xsl:text> implements </xsl:text>
            <xsl:call-template name="interfaceNames">
                <xsl:with-param name="csvs" select="@provides" />
            </xsl:call-template>
        </xsl:if>
        <xsl:text> {&#xa;</xsl:text>
        <xsl:call-template name="registerEncodingsMethod" />
        <xsl:call-template name="fields" />
        <xsl:call-template name="constructor" />
        <xsl:call-template name="getters" />
        <xsl:call-template name="fromStreamMethod" />
        <xsl:call-template name="writeMethod" />
        <xsl:call-template name="toStringMethod" />
        <xsl:text>}</xsl:text>
    </xsl:template>

    <xsl:template name="registerEncodingsMethod">
        <xsl:text>    static void registerEncodings() {&#xa;</xsl:text>
        <xsl:text>        registerEncoding(Ulong.parseUlong("</xsl:text>
        <xsl:value-of select="descriptor/@code" />
        <xsl:text>"), </xsl:text>
        <xsl:call-template name="className">
            <xsl:with-param name="name" select="@name" />
        </xsl:call-template>
        <xsl:text>::fromStream);&#xa;</xsl:text>
        <xsl:text>    }&#xa;&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="fields">
        <xsl:for-each select="field">
            <xsl:text>    private </xsl:text>
            <xsl:call-template name="className">
                <xsl:with-param name="name" select="@type" />
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text>;&#xa;</xsl:text>
        </xsl:for-each>
        <xsl:text>&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="constructor">
        <xsl:text>    public </xsl:text>
        <xsl:call-template name="className">
            <xsl:with-param name="name" select="@name" />
        </xsl:call-template>
        <xsl:text>(</xsl:text>
        <xsl:for-each select="field">
            <xsl:if test="position()>1">
                <xsl:text>,</xsl:text>
            </xsl:if>
            <xsl:call-template name="className">
                <xsl:with-param name="name" select="@type" />
            </xsl:call-template>
            <xsl:text> </xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
        </xsl:for-each>
        <xsl:text>) {&#xA;</xsl:text>
        <xsl:for-each select="field">
            <xsl:text>        this.</xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text> = </xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text>;&#xa;</xsl:text>
        </xsl:for-each>
        <xsl:text>    }&#xA;&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="getters">
        <xsl:for-each select="field">
            <xsl:text>    public </xsl:text>
            <xsl:call-template name="className">
                <xsl:with-param name="name" select="@type" />
            </xsl:call-template>
            <xsl:text> get</xsl:text>
            <xsl:call-template name="className">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text>() {&#xa;</xsl:text>
            <xsl:text>        return </xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text>;&#xa;</xsl:text>
            <xsl:text>    }&#xa;</xsl:text>
            <xsl:text>&#xa;</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="fromStreamMethod">
        <xsl:text>    public static </xsl:text>
        <xsl:call-template name="className">
            <xsl:with-param name="name" select="@name" />
        </xsl:call-template>
        <xsl:text> fromStream(InputStream is) throws IOException {&#xA;</xsl:text>
        <xsl:choose>
            <xsl:when test="@source='list'">
                <xsl:text>        List l = List.fromStream(is);&#xA;</xsl:text>
                <xsl:text>        Iterator&lt;IValue&gt; i = l.getList().iterator();&#xA;</xsl:text>
                <xsl:for-each select="field">
                    <xsl:text>        </xsl:text>
                    <xsl:call-template name="className">
                        <xsl:with-param name="name"
                            select="@type" />
                    </xsl:call-template>
                    <xsl:text> </xsl:text>
                    <xsl:call-template name="memberName">
                        <xsl:with-param name="name"
                            select="@name" />
                    </xsl:call-template>
                    <xsl:text> = i.hasNext() ? ValueUtil.cast(i.next(), </xsl:text>
                    <xsl:call-template name="className">
                        <xsl:with-param name="name"
                            select="@type" />
                    </xsl:call-template>
                    <xsl:text>.class) : null;&#xA;</xsl:text>
                </xsl:for-each>
                <xsl:text>        return new </xsl:text>
                <xsl:call-template name="className">
                    <xsl:with-param name="name" select="@name" />
                </xsl:call-template>
                <xsl:text>(</xsl:text>
                <xsl:for-each select="field">
                    <xsl:if test="position()>1">
                        <xsl:text>,</xsl:text>
                    </xsl:if>
                    <xsl:call-template name="memberName">
                        <xsl:with-param name="name"
                            select="@name" />
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:text>);&#xA;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>        return null;&#xA;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>    }&#xa;&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="writeMethod">
        <xsl:text>    public void write(OutputStream os) throws IOException {&#xA;</xsl:text>
        <xsl:text>        os.write(0);&#xA;</xsl:text>
        <xsl:text>        Ulong.parseUlong("</xsl:text>
        <xsl:value-of select="descriptor/@code" />
        <xsl:text>").write(os);&#xa;</xsl:text>
        <xsl:choose>
            <xsl:when test="@source='list'">
                <xsl:text>        java.util.List&lt;IValue&gt; list = new java.util.LinkedList&lt;&gt;();&#xA;</xsl:text>
                <xsl:for-each select="field">
                    <xsl:sort select="position()" order="descending" />
                    <xsl:text>        if (null != </xsl:text>
                    <xsl:call-template name="memberName">
                        <xsl:with-param name="name"
                            select="@name" />
                    </xsl:call-template>
                    <xsl:text> || !list.isEmpty()) {&#xA;</xsl:text>
                    <xsl:text>            list.add(0,</xsl:text>
                    <xsl:call-template name="memberName">
                        <xsl:with-param name="name"
                            select="@name" />
                    </xsl:call-template>
                    <xsl:text>);&#xA;</xsl:text>
                    <xsl:text>        }&#xA;</xsl:text>
                </xsl:for-each>
                <xsl:text>        new List(list).write(os);&#xA;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>        return null;&#xA;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>    }&#xa;&#xa;</xsl:text>
    </xsl:template>

    <xsl:template name="toStringMethod">
        <xsl:text>    public java.lang.String toString() {&#xA;</xsl:text>
        <xsl:text>        return "</xsl:text>
        <xsl:call-template name="className">
            <xsl:with-param name="name" select="@name" />
        </xsl:call-template>
        <xsl:text>[" + &#xa;</xsl:text>
        <xsl:for-each select="field">
            <xsl:text>                "</xsl:text>
            <xsl:if test="position()>1">
                <xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text>:" + </xsl:text>
            <xsl:call-template name="memberName">
                <xsl:with-param name="name" select="@name" />
            </xsl:call-template>
            <xsl:text> + &#xa;</xsl:text>
        </xsl:for-each>
        <xsl:text>                "]";&#xA;</xsl:text>
        <xsl:text>    }&#xA;</xsl:text>
    </xsl:template>

    <xsl:template name="memberName">
        <xsl:param name="name" />
        <xsl:choose>
            <xsl:when test="contains($name,'-')">
                <xsl:value-of select="substring-before($name,'-')" />
                <xsl:call-template name="className">
                    <xsl:with-param name="name"
                        select="substring-after($name,'-')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$name" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="className">
        <xsl:param name="name" />
        <xsl:choose>
            <xsl:when test="$name='*'">
                <xsl:text>I</xsl:text>
                <xsl:call-template name="className">
                    <xsl:with-param name="name" select="@requires" />
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($name,'-')">
                <xsl:call-template name="camelCase">
                    <xsl:with-param name="word"
                        select="substring-before($name,'-')" />
                </xsl:call-template>
                <xsl:call-template name="className">
                    <xsl:with-param name="name"
                        select="substring-after($name,'-')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="camelCase">
                    <xsl:with-param name="word" select="$name" />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="camelCase">
        <xsl:param name="word" />
        <xsl:value-of
            select="translate(substring($word,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')" />
        <xsl:value-of select="substring($word,2,string-length($word)-1)" />
    </xsl:template>

    <xsl:template name="interfaceNames">
        <xsl:param name="csvs" />
        <xsl:choose>
            <xsl:when test="contains($csvs,',')">
                <xsl:text>I</xsl:text>
                <xsl:call-template name="className">
                    <xsl:with-param name="name"
                        select="substring-before($csvs,',')" />
                </xsl:call-template>
                <xsl:text>, </xsl:text>
                <xsl:call-template name="interfaceNames">
                    <xsl:with-param name="csvs"
                        select="substring-after($csvs,',')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($csvs,' ')">
                <xsl:call-template name="interfaceNames">
                    <xsl:with-param name="csvs"
                        select="substring-after($csvs,' ')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>I</xsl:text>
                <xsl:call-template name="className">
                    <xsl:with-param name="name" select="$csvs" />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>