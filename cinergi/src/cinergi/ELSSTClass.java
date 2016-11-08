package cinergi;

public class ELSSTClass
{
    private String[] parents;

    private String preferredTerm;

    private String link;

    private String keyword;

    public String[] getParents ()
    {
        return parents;
    }

    public void setParents (String[] parents)
    {
        this.parents = parents;
    }

    public String getPreferredTerm ()
    {
        return preferredTerm;
    }

    public void setPreferredTerm (String preferredTerm)
    {
        this.preferredTerm = preferredTerm;
    }

    public String getLink ()
    {
        return link;
    }

    public void setLink (String link)
    {
        this.link = link;
    }

    public String getKeyword ()
    {
        return keyword;
    }

    public void setKeyword (String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public String toString()
    {
        return "ELSSTClass [parents = "+parents+", preferredTerm = "+preferredTerm+", link = "+link+", keyword = "+keyword+"]";
    }
}